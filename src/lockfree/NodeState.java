package lockfree;

public class NodeState <E extends Comparable<E>> {
    Node<E> parent = null;
    int degree;
    ISLList<Node<E>> children;
    Node<E> next;
    int seq;
    Label label = null;

    public NodeState(Node<E> parent, int degree, ISLList<Node<E>> children, Node<E> next, int seq, Label label) {
        this.parent = parent;
        this.degree = degree;
        this.children = children;
        this.next = next;
        this.seq = seq;
        this.label = label;
    }

}
