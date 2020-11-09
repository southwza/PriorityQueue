package lockfree;

public class MergeNext <E extends Comparable<E>> extends Label {
    final Node<E> a;
    final Node<E> b; // then new 'child' node
    final NodeState<E> bState;

    public MergeNext(Node<E> a, Node<E> b, NodeState<E> bState) {
        this.a = a;
        this.b = b;
        this.bState = bState;
    }
}
