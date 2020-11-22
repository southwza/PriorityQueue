package lowe_lockfree_queue;

import java.util.LinkedList;

public class MinList<E extends Comparable> {
    private int max_size;
    private int size = 0;
    private boolean removedElement = false;
    private LinkedList<E> minList;
    private E minDiscard = null;

    public MinList(int max_size) {
        this.max_size = max_size;
        minList = new LinkedList<>();
    }

    public void clear() {
        size = 0;
        minList = new LinkedList<>();
        minDiscard = null;
        removedElement = false;
    }

    public boolean isEmpty() {
        return minList.isEmpty() && !removedElement;
    }

    public void insert(E e) {
        if (minDiscard != null && e.compareTo(minDiscard) >= 0) {
            //we can't store a value larger than the minimum discard
            return;
        }

        int index = 0;
        while (index < max_size) {
            if (minList.size() == index) {
                minList.offerLast(e);
                size++;
                break;
            } else if (e.compareTo(minList.get(index)) < 0) {
                minList.add(index, e);
                size++;
                break;
            }
            index++;
        }
        //If we're exceeding our max_size, then we need to discard the largest element
        //and save it as the minDiscard.
        if (size > max_size) {
            minDiscard = minList.pollLast();
            size--;
        }

    }
    public E removeFirst() {
        size--;
        removedElement = true; //this ensures that we don't confuse an empty heap with an exhausted minList
        return minList.pollFirst();
    }
}
