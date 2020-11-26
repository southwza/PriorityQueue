package lowe_lockfree_queue;

public class Delete <E extends Comparable<E>> implements Label {
    final Node<E> pred;
    final long ts = java.lang.System.nanoTime();

    public Delete(Node<E> pred) {
        this.pred = pred;
    }
}
