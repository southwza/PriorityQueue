package queues;
import java.util.AbstractQueue;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.locks.ReentrantLock;

import Interfaces.IPriorityQueue;
import org.apache.commons.math3.exception.NullArgumentException;

/**
 * Basic implementation of a Priority Queue that is not thread safe. This is
 * implemented in the Java style where instead of passing in a separate object
 * and priority value, only the objects need to be passed in and they must be
 * Comparable.
 * 
 * Implemented with a binary heap s.t. the first element is at index 1 and
 * child elements for any element at position i are: (2 * i) and ((2 * i) + 1)
 * 
 * Supported operations:
 *  -enqueue/offer O(logN)
 *  -dequeue/poll O(logN)
 *  -peek O(1)
 *  -size O(1)
 * @author Zeid Ayssa, Zach Southwell
 *
 * References:
 * - java.util.PriorityQueue - Bloch & Lea
 * - https://en.wikipedia.org/wiki/Priority_queue
 * 
 * @param <E>
 */
@SuppressWarnings("unchecked")
public class CoarseLockBasedPriorityQueue<E extends Comparable<E>> extends AbstractQueue<E> implements IPriorityQueue<E> {

    private int size = 0;
    private Object[] heapArray;
    private static final int DEFAULT_INITIAL_CAPACITY = 10;
    private ReentrantLock heapLock = new ReentrantLock();

    public CoarseLockBasedPriorityQueue() {
        heapArray = new Object[DEFAULT_INITIAL_CAPACITY];
    }

    @Override
    public boolean enqueue(E e) {
        return offer(e);
    }

    @Override
    public E dequeue() {
        return poll();
    }

    @Override
    public boolean offer(E e) {
        if (e == null) {
            throw new NullArgumentException();
        }

        heapLock.lock();

        if (heapArray.length - 1 == size) {
            growArray();
        }
        heapArray[++size] = e;
        bubbleUp(size);

        heapLock.unlock();

        return true;
    }

    @Override
    public E poll() {
        E result;
        if (size == 0) {
            throw new IndexOutOfBoundsException();
        }

        heapLock.lock();

        result = (E) heapArray[1];
        heapArray[1] = heapArray[size];
        heapArray[size--] = null;

        bubbleDown();

        heapLock.unlock();
        // TODO Auto-generated method stub
        return result;
    }

    @Override
    public void clear() {
        size = 0;
        heapArray = new Object[DEFAULT_INITIAL_CAPACITY];
    }

    @Override
    public E peek() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Iterator<E> iterator() {
        throw new UnsupportedOperationException("Not implemented for this class");
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public String getImplementationName() {
        return "CoarseLockBasedPriorityQueue";
    }

    //Bubble up the element at the provided index
    private <T extends Comparable<T>> void bubbleUp(int index) {
        T element = (T) heapArray[index];
        while (index / 2 > 0) {
            T parent = (T) heapArray[index / 2];
            if (element.compareTo(parent) > 0) {
                break;
            }
            heapArray[index] = parent;
            index = index / 2;
        }
        heapArray[index] = element;
    }

    //Bubble down the element at position 1
    private <T extends Comparable<T>> void bubbleDown() {
        int position = 1;
        T element = (T) heapArray[position];

        while (true) {
            int leftPosition = position * 2;
            int rightPosition = position * 2 + 1;
            T leftChild = leftPosition > size ? null : (T) heapArray[leftPosition];
            T rightChild = rightPosition > size ? null : (T) heapArray[rightPosition];
            if (leftChild == null) {
                    break;
            } else if (rightChild == null) {
                if (element.compareTo(leftChild) <= 0) {
                    break;
                }
                heapArray[position] = leftChild;
                heapArray[leftPosition] = element;
                position = leftPosition;
                break;
            } else {
                if (leftChild.compareTo(rightChild) <= 0 && leftChild.compareTo(element) < 0) {
                    heapArray[position] = leftChild;
                    heapArray[leftPosition] = element;
                    position = leftPosition;
                    continue;
                }
                if (rightChild.compareTo(leftChild) <= 0 && rightChild.compareTo(element) < 0) {
                    heapArray[position] = rightChild;
                    heapArray[rightPosition] = element;
                    position = rightPosition;
                    continue;
                }
                break;
            }
        }
    }
    private void growArray() {
        int newSize = heapArray.length * 2;
        heapArray = Arrays.copyOf(heapArray, newSize);
    }

    @Override
    public String toString() {
        String result = "";
        int depth = 1;
        int size = this.size;
        while (size > 1) {
            size = size >>> 1;
            depth++;
        }
        for (int d = 0; d < depth; d++) {
            String spacer = " ".repeat((depth - d));
            for (int i = (1 << d); i < (1 << (d+1)); i++) {
                if (i > this.size) {
                    break;
                }
                result += spacer + String.format("%2d", heapArray[i]);
            }
            result += System.lineSeparator();
        }
        return result;
    }


}
