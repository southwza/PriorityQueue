package lowe_lockfree_queue;

import org.apache.commons.math3.exception.NullArgumentException;

import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

//This is an implementation of a lock-free binomial heap based on the specification by
//Gavin Lowe at http://www.cs.ox.ac.uk/people/gavin.lowe/BinomialHeap/TR.pdf
@SuppressWarnings("unchecked")
public class BinomialHeap <E extends Comparable<E>> {

    Node<E> head = new Node<>(null); //sentinel head node
    private final AtomicInteger opCount = new AtomicInteger(0);
    private final AtomicInteger count = new AtomicInteger(0);
    private static final ThreadLocal<Backoff> backoff = new ThreadLocal<Backoff>() {
        @Override
        public Backoff initialValue() {
            return new Backoff(400, 300000, 1.2f);
        }
    };

    // This number determines how often a thread attempts to tidy the heap
    // structure. A smaller number results in more tidying, which results in fewer
    // root nodes and faster traversal of the queue, but also results in more work
    // to tidy up the queue. The author suggests a value between 2 and 8 for optimal
    // performance.
    private static final int tidyRatio = 4;


    private void merge(Node<E> a, NodeState<E> aState, Node<E> pred, NodeState<E> pState, Node<E> b, NodeState<E> bState) {
        MergeNext<E> pLabel = a != pred ? new MergeNext<>(a, b, bState) : null;
        MergeParent<E> mergeParent = new MergeParent<>(pred, pState, pLabel, b, bState);
        NodeState<E> aStateL = new NodeState<>(aState.parent, aState.degree, aState.children, aState.next, aState.seq, mergeParent);

        if (pred.getState() == pState && b.getState() == bState && a.compareAndSet(aState, aStateL)) {
            if (a != pred) {
                mergeLabelPred(a, aStateL, pred, pState, pLabel, b, bState);
            } else {
                mergeUpdateB(a, aStateL, pred, pState, b, bState);
            }
        }
    }

    private void mergeLabelPred(Node<E> a, NodeState<E> aStateL, Node<E> pred, NodeState<E> pState, MergeNext<E> pLabel, Node<E> b, NodeState<E> bState) {
        NodeState<E> pStateL = new NodeState<>(pState.parent, pState.degree, pState.children, pState.next, pState.seq, pLabel);
        if (b.getState() == bState && pred.compareAndSet(pState, pStateL)) {
            mergeUpdateB(a, aStateL, pred, pStateL, b, bState);
        } else {
            if (a.getState() == aStateL && pred.getState().label != pLabel &&b.getState().parent != a) {
                NodeState<E> newAState = new NodeState<>(aStateL.parent, aStateL.degree, aStateL.children, aStateL.next, aStateL.seq, null);
                a.compareAndSet(aStateL, newAState);
            }
        }
    }

    private void mergeUpdateB(Node<E> a, NodeState<E> aStateL, Node<E> pred, NodeState<E> pStateL, Node<E> b, NodeState<E> bState) {
        Node<E> newNext = aStateL.children.isEmpty() ? null : aStateL.children.head();
        NodeState<E> bStateU = new NodeState<>(a, bState.degree, bState.children, newNext, bState.seq + 1, null);
        if (b.getState() == bState && b.compareAndSet(bState, bStateU)) {
            if (a != pred) {
                mergeUpdatePred(a, aStateL, pred, pStateL, b, bState.next);
            } else {
                mergeUpdateAPred(a, aStateL, b, bState.next);
            }
        } else {
            if (b.getState().parent != a) { //backtrack
                if (a != pred) {
                    NodeState<E> newPStateL = new NodeState<>(pStateL.parent, pStateL.degree, pStateL.children, pStateL.next, pStateL.seq, null);
                    if (pred.getState() == pStateL) {
                        pred.compareAndSet(pStateL, newPStateL);
                    }
                }
                if (a.getState() == aStateL) {
                    NodeState<E> newAStateL = new NodeState<>(aStateL.parent, aStateL.degree, aStateL.children, aStateL.next, aStateL.seq, null);
                    a.compareAndSet(aStateL, newAStateL);
                }
            }
        }
    }
    private void mergeUpdatePred(Node<E> a, NodeState<E> aStateL, Node<E> pred, NodeState<E> pStateL, Node<E> b,
            Node<E> nextNode) {
        NodeState<E> pStateU = new NodeState<>(pStateL.parent, pStateL.degree, pStateL.children, nextNode, pStateL.seq, null);
        if (pred.getState() == pStateL && pred.compareAndSet(pStateL, pStateU)) {
            mergeUpdateA(a, aStateL, b);
        }
    }
    private void mergeUpdateA(Node<E> a, NodeState<E> aStateL, Node<E> b) {
        ISLList<Node<E>> newChildren = new ISLList<>(b, aStateL.children);
        NodeState<E> aStateU = new NodeState<>(aStateL.parent, aStateL.degree + 1, newChildren, aStateL.next, aStateL.seq, null);
        if (a.getState() == aStateL) {
            a.compareAndSet(aStateL, aStateU);
        }
    }
    private void mergeUpdateAPred(Node<E> a, NodeState<E> aStateL, Node<E> b, Node<E> nextNode) {
        ISLList<Node<E>> newChildren = new ISLList<>(b, aStateL.children);
        NodeState<E> aStateU = new NodeState<>(aStateL.parent, aStateL.degree + 1, newChildren, nextNode, aStateL.seq, null);
        if (a.getState() == aStateL) {
            a.compareAndSet(aStateL, aStateU);
        }
    }

    private void help(Node<E> helpNode, NodeState<E> helpState) {

        if (helpState.label instanceof MergeParent) {
            MergeParent<E> label = (MergeParent<E>) helpState.label;
            if (label.b.getState().parent == helpNode) { //b update has happened
                NodeState<E> newPState = label.pred.getState();
                if (helpNode == label.pred) {
                    mergeUpdateAPred(helpNode, helpState, label.b, label.bState.next);
                } else if (newPState.label == label.pLabel) { //pred update has not happened
                    mergeUpdatePred(helpNode, helpState, label.pred, newPState, label.b, label.bState.next);
                } else { //pred update has happened
                    mergeUpdateA(helpNode, helpState, label.b);
                }
            } else {
                NodeState<E> newPState = label.pred.getState();
                if (helpNode != label.pred && (newPState.label != label.pLabel)) {
                    mergeLabelPred(helpNode, helpState, label.pred, label.predState, label.pLabel, label.b, label.bState);
                } else {
                    mergeUpdateB(helpNode, helpState, label.pred, newPState, label.b, label.bState);
                }
            }
        }

        if (helpState.label instanceof MergeNext) {
            MergeNext<E> label = (MergeNext<E>) helpState.label;
            NodeState<E> newAState = label.a.getState();
            if (newAState.label instanceof MergeParent) {
                MergeParent<E> mp = (MergeParent<E>) newAState.label;
                if (mp.pLabel != label) {
                    return;
                }
                if (label.b.getState().parent == label.a) { //b update has happened
                    mergeUpdatePred(label.a, newAState, helpNode, helpState, mp.b, mp.bState.next);
                } else {
                    mergeUpdateB(label.a, newAState, helpNode, helpState, label.b, label.bState);
                }
            }
            
        }

        if (helpState.label instanceof Delete) {
            Delete<E> label = (Delete<E>) helpState.label;
            helpDelete(label.pred, helpNode, helpState);
        }
    }

    private boolean delete(Node<E> pred, Node<E> delNode, NodeState<E> delState) {
        NodeState<E> delStateL = labelForDelete(pred, delNode, delState, delState.next);
        if (delStateL != null) {
            completeDelete(pred, delNode, delStateL);
            return true;
        }
        return false;
    }

    private NodeState<E> labelForDelete(Node<E> pred, Node<E> delNode, NodeState<E> delState, Node<E> newNext) {
        Delete<E> deleteLabel = new Delete<>(pred);
        NodeState<E> delStateL = new NodeState<>(delState.parent, delState.degree, delState.children, newNext, delState.seq + 1, deleteLabel);
        if (delNode.getState() == delState && delNode.compareAndSet(delState,  delStateL)) {
            return delStateL;
        }
        return null;
    }

    private boolean deleteWithParent(Node<E> pred, Node<E> delNode, NodeState<E> delState, Node<E> pPred, Node<E> parent, NodeState<E> pState) {
        Node<E> newNext = (delState.next == null) ? pState.next : delState.next;
        NodeState<E> delStateL = labelForDelete(pred, delNode, delState, newNext);
        if (delStateL != null) {
            helpDelete(pPred, parent, pState);
            completeDelete(pred, delNode, delStateL);
            return true;
        }
        return false;
    }

    private void completeDelete(Node<E> pred, Node<E> delNode, NodeState<E> delStateL) {
        ISLList<Node<E>> children = delStateL.children;
        Node<E> next = delStateL.next;
        if (children.isEmpty()) {
            predUpdate(pred, delNode, next);
        } else {
            Node<E> lastC = children.last();
            NodeState<E> lastCState = lastC.getState();
            if (lastCState.parent == delNode && lastCState.next != next) {
                NodeState<E> newCState = new NodeState<>(lastCState.parent, lastCState.degree, lastCState.children, next, lastCState.seq, lastCState.label);
                if (lastC.getState() == lastCState) {
                    lastC.compareAndSet(lastCState, newCState);
                }
            }
            //Update predecessor
            predUpdate(pred, delNode, children.head());

            //Update children
            for (Node<E> c : children) {
                NodeState<E> cState = c.getState();
                if (cState.parent == delNode) {
                    NodeState<E> newCState = new NodeState<>(null, cState.degree, cState.children, cState.next, cState.seq, cState.label);
                    c.compareAndSet(cState, newCState);
                }
            }
        }
    }
    private void predUpdate(Node<E> pred, Node<E> delNode, Node<E> newNext) {
        NodeState<E> predState = pred.maybeClearParent();
        if (predState.next == delNode && predState.parent == null &&
                completePredUpdate(pred, predState, delNode, newNext)) {
            //Done
        } else {
            while (!delNode.deleted) {
                Object[] pTuple = findPred(delNode);
                Node<E> p = (Node<E>) pTuple[0];
                NodeState<E> pState = (NodeState<E>) pTuple[1];
                if (p != delNode) {
                    if (!delNode.deleted) {
                        completePredUpdate(p, pState, delNode, newNext);
                    }
                }
            }
        }
    }

    private boolean completePredUpdate(Node<E> pred, NodeState<E> predState, Node<E> delNode, Node<E> newNext) {
        if (predState.label == null) {
            NodeState<E> newPredState = new NodeState<>(predState.parent, predState.degree, predState.children, newNext, predState.seq, null);
            if (pred.getState() == predState && pred.compareAndSet(predState, newPredState)) {
                delNode.deleted = true;
                return true;
            } else if (delNode.deleted) {
                return true;
            } else {
                newPredState = pred.maybeClearParent();
                if (newPredState.next == delNode && newPredState.parent == null) {
                    return completePredUpdate(pred, newPredState, delNode, newNext); //retry
                }
                return false;
            }
        } else {
            help(pred, predState);
            return false;
        }
    }

    private void helpDelete(Node<E> pred, Node<E> helpNode, NodeState<E> helpState) {
        if (!helpNode.deleted) {
            Node<E> parent = helpState.parent;
            if (parent != null) {
                NodeState<E> pState = parent.getState();
                Node<E> pPred = ((Delete<E>)pState.label).pred;
                helpDelete(pPred, parent, pState);
                completeDelete(pPred, helpNode, helpNode.maybeClearParent());
            } else {
                completeDelete(pred, helpNode, helpState);
            }
        }
    }

    private Object[] advance(Node<E> curr, Integer currSeq) {
        Object[] result = null;
        while(true) {
            NodeState<E> currState = curr.getState();
            if (currState.seq != currSeq) {
                return null;
            } else {
                if (currState.label instanceof MergeParent) {
                    MergeParent<E> mp = (MergeParent<E>) currState.label;
                    if (mp.pred == curr) {
                        result = skipMerge(curr, mp.b, mp.bState);
                    } else {
                        result = skipDeleted(currState.next);
                    }
                } else if (currState.label instanceof MergeNext) {
                    MergeNext<E> mn = (MergeNext<E>) currState.label;
                    result = skipMerge(mn.a, mn.b, mn.bState);
                } else {
                    result = skipDeleted(currState.next);
                }
            }
            if (curr.getState() == currState) {
                return result;
            }
        }
    }

    private Object[] skipDeleted(Node<E> n) {
        Object[] result = new Object[3];
        if (n == null) {
            result[0] = null;
            result[1] = -1;
            result[2] = new ISLList<Node<E>>();
        } else {
            NodeState<E> nState = n.getState();
            if (nState.label instanceof Delete) {
                result = skipDeleted(nState.next);
                result[2] = new ISLList<>(n, (ISLList<Node<E>>) result[2]);

            } else {
                result[0] = n;
                result[1] = nState.seq;
                result[2] = new ISLList<Node<E>>();
            }
        }
        return result;
    }

    private Object[] skipMerge(Node<E> a, Node<E> b, NodeState<E> bState) {
        Object[] result = skipDeleted(bState.next);
        NodeState<E> myBState = b.getState();
        if (myBState == bState) {
            result[2] = new ISLList<>(b, (ISLList<Node<E>>) result[2]);
            return result;
        } else if (myBState.parent == a) {
            return result;
        } else if (myBState.label instanceof Delete) {
            result = skipDeleted(myBState.next);
            result[2] = new ISLList<>(b, (ISLList<Node<E>>) result[2]);
        } else {
            result[0] = b;
            result[1] = myBState.seq;
            result[2] = new ISLList<Node<E>>();
        }
        return result;
    }

    private Object[] findPred(Node<E> delNode) {
        Object[] returnVal;
        Node<E> curr = this.head;
        int currSeq = curr.getSeq();
        while (curr != null) {
            Object[] nodeInfo = advance(curr, currSeq);
            if (nodeInfo == null) {
                //restart
                curr = this.head;
                currSeq = curr.getSeq();
            } else {
                Node<E> next = (Node<E>) nodeInfo[0];
                Integer nextSeq = (Integer) nodeInfo[1];
                ISLList<Node<E>> sn = (ISLList<Node<E>>) nodeInfo[2];
                Node<E> pred = curr;
                while (!sn.isEmpty() && sn.head() != delNode ) {
                    pred = sn.head();
                    sn = sn.tail();
                }
                if (!sn.isEmpty()) { //sn.head == delNode
                    NodeState<E> pState = pred.maybeClearParent();
                    if (pState.next != delNode) {
                        if (pred == curr && pState.label != null) {
                            help(pred, pState);
                            pState = pred.maybeClearParent();
                            if (pState.next == delNode && pState.parent == null) {
                                returnVal = new Object[2];
                                returnVal[0] = pred;
                                returnVal[1] = pState;
                                return returnVal;
                            } else {
                                //restart
                                curr = this.head;
                                currSeq = curr.getSeq();
                            }
                        } else {
                            //restart
                            curr = this.head;
                            currSeq = curr.getSeq();
                        }
                    } else if (pState.parent == null) {
                        returnVal = new Object[2];
                        returnVal[0] = pred;
                        returnVal[1] = pState;
                        return returnVal;
                    } else { //need to help pState.parent
                        Node<E> parent = pState.parent;
                        NodeState<E> parentState = parent.getState();
                        Node<E> pPred = ((Delete<E>)parentState.label).pred;
                        helpDelete(pPred, parent, parentState);
                        pState = pred.maybeClearParent();
                        if (pState.next == delNode && pState.parent == null) {
                            returnVal = new Object[2];
                            returnVal[0] = pred;
                            returnVal[1] = pState;
                            return returnVal;
                        } else {
                            curr = this.head;
                            currSeq = curr.getSeq();
                        }
                    }
                } else {
                    curr = next;
                    currSeq = nextSeq;
                }
            }
        }
        delNode.deleted = true;
        returnVal = new Object[2];
        returnVal[0] = delNode;
        returnVal[1] = null;
        return returnVal;
    }

    public void insert(E e) {
        //In this implementation, null is not allowed
        if (e == null) {
            throw new NullArgumentException();
        }
        backoff.get().reset();
        Node<E> myNode = new Node<>(e);
        Node<E> curr = head;
        NodeState<E> currState;
        while (true) {
            currState = curr.maybeClearParent();
            if (currState.parent != null) {
                curr = currState.parent;
            } else if(currState.label == null) {
                if (currState.next == null) { //at last node
                    NodeState<E> newCurrState = new NodeState<>(null, currState.degree, currState.children, myNode, currState.seq, null);
                    if (curr.getState() == currState) {
                        if (curr.compareAndSet(currState, newCurrState)) {
                            count.incrementAndGet();
                            maybeTidy();
                            return;
                        } else {
                            backoff.get().backoff();
                        }
                    }
                } else if (currState.degree == 0 && curr.key != null && curr.key.compareTo(e) <= 0) { //insert below curr
                    myNode.getState().parent = curr;
                    ISLList<Node<E>> newChildren = new ISLList<>();
                    newChildren = new ISLList<>(myNode, newChildren);
                    NodeState<E> newCurrState = new NodeState<>(currState.parent, 1, newChildren, currState.next, currState.seq, currState.label);
                    if (curr.getState() == currState && curr.compareAndSet(currState, newCurrState)) {
                        count.incrementAndGet();
                        maybeTidy();
                        return;
                    } else {
                        myNode.getState().parent = null;
                    }
                } else {
                    curr = currState.next;
                }
            } else {
                Label label = currState.label;
                if (label instanceof MergeNext) {
                    MergeNext<E> mn = (MergeNext<E>) label;
                    //Try to skip over the next node if it is being merged
                    NodeState<E> myBState = mn.b.getState();
                    Node<E> next = mn.bState.next;
                    if (next != null) {
                        if (myBState == mn.bState || myBState.parent == mn.a) {
                            curr = next;
                        } else {
                            curr = mn.b;
                        }
                    } else if (myBState.parent == mn.a) {
                        help(curr, currState);
                    } else {
                        curr = mn.b;
                    }
                } else if (label instanceof MergeParent) {
                    MergeParent<E> mp = (MergeParent<E>) label;
                    if (curr == mp.pred) {
                        //Try to skip over the next node if it is being merged
                        NodeState<E> myBState = mp.b.getState();
                        Node<E> next = mp.bState.next;
                        if (next != null) {
                            if (myBState == mp.bState || myBState.parent == curr) {
                                curr = next;
                            } else {
                                curr = mp.b;
                            }
                        } else if (myBState.parent == curr) {
                            help(curr, currState);
                        } else {
                            curr = mp.b;
                        }
                    } else if (currState.next != null) {
                        curr = currState.next;
                    } else {
                        help(curr, currState);
                    }
                } else if (label instanceof Delete) {
                    Delete<E> del = (Delete<E>) label;
                    if (currState.next != null) {
                        curr = currState.next;
                    } else {
                        helpDelete(del.pred, curr, currState);
                        curr = head;
                    }
                }
            }
        }
    }

    public E minimum() {
        MinList<Node<E>> minList = new MinList<>(10);
        Node<E> curr = head;
        int currSeq = curr.getSeq();
        long startTime = java.lang.System.nanoTime();
        while (true) {
            Object[] nodeInfo = advance(curr, currSeq);
            if (nodeInfo == null) {
                //restart
                curr = head;
                currSeq = curr.getSeq();
                minList.clear();
            } else {
                Node<E> next = (Node<E>) nodeInfo[0];
                Integer nextSeq = (Integer) nodeInfo[1];
                ISLList<Node<E>> skipNodes = (ISLList<Node<E>>) nodeInfo[2];
                insertNodes(skipNodes, startTime, minList);
                if (next != null) {
                    minList.insert(next);
                    curr = next;
                    currSeq = nextSeq;
                } else if (minList.isEmpty()) {
                    return null;
                } else { //at end of list; find first non-deleted node in minList
                    Node<E> minNode = minList.removeFirst();
                    if (minNode == null) {
                        //restart
                        curr = head;
                        currSeq = curr.getSeq();
                        minList.clear();
                    } else if(!minNode.deleted) {
                        return minNode.key;
                    } else {
                        insertNodes(minNode.getState().children, startTime, minList);
                    }
                }
            }
        }
    }

    private void insertNodes(ISLList<Node<E>> ns, long startTime, MinList<Node<E>> minList) {
        for (Node<E> n : ns) {
            NodeState<E> nState = n.getState();
            if (nState.label instanceof Delete) {
                Delete<E> del = (Delete<E>) nState.label;
                if (del.ts - startTime <= 0) {
                    insertNodes(nState.children, startTime, minList);
                } else {
                    minList.insert(n);
                }
            } else {
                minList.insert(n);
            }
        }
    }

    public E deleteMin() {
        backoff.get().reset();
        MinList<NodePair<E>> minList = new MinList<>(10);
        Node<E> curr = head;
        int currSeq = curr.getSeq();
        while (true) {
            Object[] nodeInfo = advance(curr, currSeq);
            if (nodeInfo == null) {
                //restart
                backoff.get().backoff();
                curr = head;
                currSeq = curr.getSeq();
                minList.clear();
            } else {
                Node<E> next = (Node<E>) nodeInfo[0];
                Integer nextSeq = (Integer) nodeInfo[1];
                ISLList<Node<E>> skipNodes = (ISLList<Node<E>>) nodeInfo[2];
                Node<E> pred = curr;
                for (Node<E> skipNode : skipNodes) {
                    NodeState<E> skipState = skipNode.getState();
                    if (skipState.label instanceof Delete) {
                        ISLList<Node<E>> children = skipState.children;
                        insertChildren(pred, children, minList);
                        if (!children.isEmpty()) {
                            pred = children.last();
                        }
                    } else {
                        minList.insert(new NodePair<>(pred, skipNode));
                        pred = skipNode;
                    }
                }
                if (next != null) {
                    minList.insert(new NodePair<>(pred, next));
                    curr = next;
                    currSeq = nextSeq;
                } else if (minList.isEmpty()) {
                    return null;
                } else {
                    NodePair<E> minPair = minList.removeFirst();
                    if (minPair == null) {
                        //restart
                        backoff.get().backoff();
                        curr = head;
                        currSeq = curr.getSeq();
                        minList.clear();
                    } else {
                        Node<E> predMin = minPair.expectedPred;
                        Node<E> delNode = minPair.node;
                        if (tryDelete(predMin, delNode)) {
                            maybeTidy();
                            count.decrementAndGet();
                            return delNode.key;
                        } else {
                            NodeState<E> delState = delNode.getState();
                            Label label = delState.label;
                            if (label instanceof Delete) {
                                pred = ((Delete<E>)label).pred;
                                insertChildren(pred, delState.children, minList);
                            } else {
                                //restart
                                backoff.get().backoff();
                                curr = head;
                                currSeq = curr.getSeq();
                                minList.clear();
                            }
                        }
                    }
                }
            }
        }
    }

    private void insertChildren(Node<E> pred, ISLList<Node<E>> children, MinList<NodePair<E>> minList) {
        ISLList<Node<E>> cs = children;
        Node<E> p = pred;

        while (!cs.isEmpty()) {
            Node<E> c = cs.head();
            cs = cs.tail();
            minList.insert(new NodePair<>(p, c));
            p = c;
        }

    }

    private boolean tryDelete(Node<E> pred, Node<E> delNode) {
        NodeState<E> delState = delNode.maybeClearParent();
        Label label = delState.label;
        if (label != null) {
            if (label instanceof Delete) {
                return false; //fast return in this case
            } else {
                help(delNode, delState);
                return tryDelete(pred, delNode);
            }
        } else {
            Node<E> parent = delState.parent;
            if (parent != null) {
                NodeState<E> pState = parent.getState();
                Label pLabel = pState.label;
                if (pLabel instanceof Delete) {
                    Node<E> pPred = ((Delete<E>)pLabel).pred;
                    return deleteWithParent(pred, delNode, delState, pPred, parent, pState) ||
                            tryDelete(pred, delNode);
                } else {
                    return false;
                }
            } else {
                return delete(pred, delNode, delState) || tryDelete(pred, delNode);
            }
        }
    }

    private void maybeTidy() {
        if (opCount.incrementAndGet()%tidyRatio == 0) {
            tidy();
        }
    }

    private void tidy() {
        HashMap<Integer, NodePair<E>> buckets = new HashMap<>();
        Node<E> curr = head;
        boolean done = false;
        while (!done) {
            Object[] nextObj = nextStep(curr);
            Node<E> next = (Node<E>) nextObj[0];
            Boolean ok = (Boolean) nextObj[1];
            if (next == null) {
                done = true;
            } else if (ok) {
                Node<E> pred = curr;
                curr = next;
                tryMerge(pred, curr, buckets);
            } else {
                curr = next;
            }
        }
    }

    private void tryMerge(Node<E> pred, Node<E> curr, HashMap<Integer, NodePair<E>> buckets) {
        NodeState<E> currState = curr.maybeClearParent();
        int degree = currState.degree;
        if (currState.next != null && (degree != 0 || ThreadLocalRandom.current().nextInt(0, 4) == 0)) {
            NodeState<E> predState = pred.maybeClearParent();
            if (buckets.get(degree) != null) {
                NodePair<E> np = buckets.get(degree);
                Node<E> predOther = np.expectedPred;
                Node<E> other = np.node;
                NodeState<E> otherState = other.maybeClearParent();
                NodeState<E> predOtherState = predOther.maybeClearParent();
                if (other == curr || !(otherState.label == null && otherState.parent == null)) {
                    if (predState.next == curr) {
                        buckets.put(degree, new NodePair<>(pred, curr));
                    }
                } else if (otherState.degree != degree) {
                    if (predState.next == curr) {
                        buckets.put(degree, new NodePair<>(pred, curr));
                    } else {
                        buckets.put(degree, null);
                    }
                } else if (currState.label == null && currState.parent == null) { //try merging
                    if (other.key.compareTo(curr.key) <= 0) {
                        if (predState.next == curr && predState.label == null && predState.parent == null) {
                            merge(other, otherState, pred, predState, curr, currState);
                        }
                    } else if (predOtherState.next == other && predOtherState.label == null && predOtherState.parent == null) {
                        merge(curr, currState, predOther, predOtherState, other, otherState);
                    } else if (predState.next == curr) {
                        buckets.put(degree, new NodePair<>(pred, curr));
                    }
                }
            } else if (predState.next == curr) {
                buckets.put(degree, new NodePair<>(pred, curr));
            }
        }
    }

    private Object[] nextStep(Node<E> curr) {
        Object[] returnVal = new Object[2];
        NodeState<E> currState = curr.maybeClearParent();
        if (currState.parent != null) {
            returnVal[0] = currState.parent;
            returnVal[1] = false;
            return returnVal;
        } else {
            Label label = currState.label;
            if (label instanceof MergeNext) {
                MergeNext<E> mn = (MergeNext<E>) label;
                return skip(mn.a, mn.b, mn.bState);
            } else if (label instanceof MergeParent &&
                    curr == ((MergeParent<E>) label).pred) {
                MergeParent<E> mp = (MergeParent<E>) label;
                return skip(curr, mp.b, mp.bState);
            } else if (label instanceof Delete) {
                returnVal[0] = currState.next;
                returnVal[1] = false;
                return returnVal;
            } else {
                returnVal[0] = currState.next;
                returnVal[1] = true;
                return returnVal;
            }
        }
    }

    private Object[] skip(Node<E> a, Node<E> b, NodeState<E> bState) {
        Object[] returnVal = new Object[2];
        NodeState<E> myBState = b.getState();
        if (myBState == bState || myBState.parent == a) {
            returnVal[0] = bState.next;
        } else {
            returnVal[0] = b;
        }
        returnVal[1] = true;
        return returnVal;
    }

    public int size() {
        return count.get();
    }

    //This is just a debugging method to manually count each node in the heap.
    //It is not thread safe; use count() instead
    public int hardCount() {
        if (head.getState().next == null) {
            return 0;
        } else {
            return head.getState().next.topCount();
        }
    }

    public boolean isEmpty() {
        return count.get() == 0;
    }

    public void clear() {
        head = new Node<>(null); //sentinel head node
        opCount.set(0);
        count.set(0);
    }
}
