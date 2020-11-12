import Interfaces.IPriorityQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class JavaLibPriorityQueue<T> implements IPriorityQueue<T>
{
   final PriorityBlockingQueue<T> queue = new PriorityBlockingQueue<>();
   @Override
   public boolean enqueue(T e) {
      return queue.offer(e);
   }

   @Override
   public T dequeue() {
      return queue.poll();
   }

   @Override
   public int size() {
      return queue.size();
   }
}
