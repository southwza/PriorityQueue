package queues;

import Interfaces.IPriorityQueue;
import MichaelSLockBasedPriorityQueueHeap.MichaelSPriorityQueue;
import org.apache.commons.math3.exception.NullArgumentException;

public class FineLockBasedPriorityQueue<T extends Comparable<T>> implements IPriorityQueue<T>
{
   private MichaelSPriorityQueue<T> queue;

   public FineLockBasedPriorityQueue() {
      queue = new MichaelSPriorityQueue<>();
   }

   @Override
   public boolean enqueue(T e) {
      if (e == null) {
         throw new NullArgumentException();
      }

      return queue.enqueue(e);
   }

   @Override
   public T dequeue() {
      if (queue.size() == 0) {
         throw new IndexOutOfBoundsException();
      }

      return queue.dequeue();
   }

   @Override
   public int size() {
      return queue.size();
   }

   @Override
   public void clear() {
      queue.clear();
   }

   @Override
   public String getImplementationName() {
      return "FineLockBasedPriorityQueue";
   }
}
