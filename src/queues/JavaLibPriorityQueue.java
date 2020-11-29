package queues;

import Interfaces.IPriorityQueue;
import org.apache.commons.math3.exception.NullArgumentException;

import java.util.concurrent.PriorityBlockingQueue;

public class JavaLibPriorityQueue<T> implements IPriorityQueue<T>
{
   final PriorityBlockingQueue<T> queue = new PriorityBlockingQueue<>();
   @Override
   public boolean enqueue(T e) {
      if (e == null) {
         throw new NullArgumentException();
      }

      return queue.offer(e);
   }

   @Override
   public T dequeue() {
      if (queue.isEmpty()) {
         throw new IndexOutOfBoundsException();
      }
      return queue.poll();
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
      return "JavaLibPriorityQueue";
   }
}
