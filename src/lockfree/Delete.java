package lockfree;

public class Delete <E extends Comparable<E>> extends Label {
    final Node<E> pred;
    final long ts = java.lang.System.nanoTime();

    public Delete(Node<E> pred) {
        this.pred = pred;
    }
}
