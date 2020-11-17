package queues;

import Interfaces.IPriorityQueue;
import lowe_lockfree_queue.BinomialHeap;
import org.apache.commons.math3.exception.NullArgumentException;

public class LockFreePriorityQueue<T extends Comparable<T>> implements IPriorityQueue<T>
{
   private BinomialHeap<T> queue = new BinomialHeap<>();

   @Override
   public boolean enqueue(T e) {
      if (e == null) {
         throw new NullArgumentException();
      }
      queue.insert(e);
      return true;
   }

   @Override
   public T dequeue() {
      T min = queue.deleteMin();
      if (min == null) {
         System.out.println("got null!");
      }
      return min;
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
      return "queues.LockFreePriorityQueue";
   }
}
