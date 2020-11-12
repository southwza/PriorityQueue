package lockfree;

import java.util.concurrent.atomic.AtomicReference;

public class Node<E extends Comparable<E>> implements Comparable<Node<E>> {
    AtomicReference<NodeState<E>> state = new AtomicReference<>(
            new NodeState<>(null, 0, new ISLList<>(), null, 0, null));
    volatile boolean deleted = false;
    E key;

    public Node(E key) {
        this.key = key;
    }

    public NodeState<E> getState() {
        return state.get();
    }

    public boolean compareAndSet(NodeState<E> oldState, NodeState<E> newState) {
        return state.compareAndSet(oldState, newState);
    }

    public int getSeq() {
        return state.get().seq;
    }

    public NodeState<E> maybeClearParent() {
        NodeState<E> oldState = state.get();
        Node<E> p = oldState.parent;
        if (p != null && p.deleted) {
            NodeState<E> newState = new NodeState<>(null, oldState.degree, oldState.children, oldState.next, oldState.seq, oldState.label);
            if (state.get() == oldState && compareAndSet(oldState, newState)) {
                return newState;
            } else {
                return maybeClearParent(); //retry
            }
        } else {
            return oldState;
        }
    }

    @Override
    public int compareTo(Node<E> o) {
        return key.compareTo(o.key);
    }

}
