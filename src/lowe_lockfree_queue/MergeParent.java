package lowe_lockfree_queue;

public class MergeParent <E extends Comparable<E>> implements Label {
    final Node<E> pred;
    final NodeState<E> predState;
    final MergeNext<E> pLabel;
    final Node<E> b; // then new 'child' node
    final NodeState<E> bState;

    public MergeParent(Node<E> pred, NodeState<E> predState, MergeNext<E> pLabel, Node<E> b, NodeState<E> bState) {
        this.pred = pred;
        this.predState = predState;
        this.pLabel = pLabel;
        this.b = b;
        this.bState = bState;
    }

}
