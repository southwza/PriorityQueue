package lowe_lockfree_queue;

public class NodeState <E extends Comparable<E>> {
    Node<E> parent;
    int degree;
    ISLList<Node<E>> children;
    Node<E> next;
    int seq;
    Label label;

    public NodeState(Node<E> parent, int degree, ISLList<Node<E>> children, Node<E> next, int seq, Label label) {
        this.parent = parent;
        this.degree = degree;
        this.children = children;
        this.next = next;
        this.seq = seq;
        this.label = label;
    }

}
