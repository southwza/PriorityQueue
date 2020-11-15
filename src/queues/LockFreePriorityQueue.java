package queues;

import Interfaces.IPriorityQueue;
import org.apache.commons.math3.exception.NullArgumentException;

public class LockFreePriorityQueue<T> implements IPriorityQueue<T>
{
   private int size = 0;

   @Override
   public boolean enqueue(T e) {
      if (e == null) {
         throw new NullArgumentException();
      }
      // TODO: implementation
      return false;
   }

   @Override
   public T dequeue() {
      if (size == 0) {
         throw new IndexOutOfBoundsException();
      }
      // TODO: implementation
      return null;
   }

   @Override
   public int size() {
      // TODO: implementation
      return 0;
   }

   @Override
   public void clear() {
      // TODO: implementation
   }

   @Override
   public String getImplementationName() {
      return "queues.LockFreePriorityQueue";
   }
}
