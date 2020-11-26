package lowe_lockfree_queue;

public class NodePair <E extends Comparable<E>> implements Comparable<NodePair<E>> {
    public Node<E> node;
    public Node<E> expectedPred;

    public NodePair(Node<E> expectedPred, Node<E> node) {
        this.node = node;
        this.expectedPred = expectedPred;
    }

    @Override
    public int compareTo(NodePair<E> o) {
        return node.compareTo(o.node);
    }
    
}
