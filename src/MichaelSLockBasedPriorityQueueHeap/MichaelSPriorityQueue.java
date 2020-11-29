package MichaelSLockBasedPriorityQueueHeap;

import org.apache.commons.math3.exception.NullArgumentException;

public class MichaelSPriorityQueue<T extends Comparable<T>>
{
   private Heap<T> heap;
   public MichaelSPriorityQueue() {
      heap = new Heap<>();
   }

   public boolean enqueue(T e) {
      if (e == null) {
         throw new NullArgumentException();
      }

      heap.concurrentInsert(e);
      return true;
   }

   public T dequeue() {
      if (heap.size() == 0) {
         throw new IndexOutOfBoundsException();
      }

      return heap.concurrentDelete();
   }

   public int size() {
      return heap.size();
   }

   public void clear() {
      heap.clear();
   }
}
