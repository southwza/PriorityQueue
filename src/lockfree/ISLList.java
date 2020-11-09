package lockfree;

import java.util.Iterator;

// Simple implementation of a scala-like singly linked immutable list. Need to
// use an immutable data structure and the jdk does not provide it :(
// For simplicity:
// - a null value means an empty list
// - all lists will have an empty sentinal at the end
// - therefore any list with a non-null value must have a non-null 'next'

public class ISLList <E> implements Iterable<E> {

    final private E value;
    final private ISLList<E> next;

    public ISLList() {
        //create a new empty list;
        value = null;
        next = null;
    }

    public ISLList(E e, ISLList<E> next) {
        if (e == null) {
            throw new NullPointerException("Null value not allowed in ISLList");
        }
        if (next == null) {
            throw new NullPointerException("Null value not allowed for next param");
        }
        this.value = e;
        this.next = next;
    }

    public E head() {
        return value;
    }

    public ISLList<E> tail() {
        return next;
    }

    public boolean isEmpty() {
        return value == null;
    }

    public E last() {
        E returnVal = null;
        ISLList<E> list = this; 
        while (list.value != null) {
            returnVal = list.value;
            list = list.next; //this value cannot be null because value was not null
        }
        return returnVal;
    }

    @Override
    public Iterator<E> iterator() {
        return new ISLListIterator(this);
    }

    class ISLListIterator implements Iterator<E> {

        ISLList<E> curr;
        public ISLListIterator(ISLList<E> curr) {
            this.curr = curr;
        }

        @Override
        public boolean hasNext() {
            return curr.next != null && curr.next.next != null;
        }

        @Override
        public E next() {
            curr = curr.next;
            if (curr == null) {
                return null;
            }
            return curr.value;
        }
    }
}
