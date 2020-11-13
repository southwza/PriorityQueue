package bench;

import Interfaces.IPriorityQueue;
import org.openjdk.jmh.annotations.*;
import unsynchronized.ASPriorityQueue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.Assert.*;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
public class BenchMarkWorstCase
{
   @Param({
         ""
   })
   public String testValue;

   @Setup
   public void setup() {
      // Read test file
   }

   @Benchmark
   public void benchLockBasedPriorityQueue() {
      // code to benchmark goes here
      System.out.println("Benchmarking " + testValue);
   }

   @Benchmark
   public void benchLockFreePriorityQueue() {
      // code to benchmark goes here
      System.out.println("Benchmarking " + testValue);
   }

   private void performTestCaseAction(int numOfThreads, IPriorityQueue<Integer> queue, List<Integer> inputValues) {
      ExecutorService es = Executors.newFixedThreadPool(numOfThreads);
      final ArrayList<Future<?>> listOfFutures = new ArrayList<>();

      // Enqueue
      for (Integer value: inputValues) {
         final Future<?> future = es.submit(() -> {
            assertTrue(queue.enqueue(value));
         });

         listOfFutures.add(future);
      }

      waitForFutures(listOfFutures);

      assertEquals(queue.size(), listOfFutures.size());

      // Clear futures
      listOfFutures.clear();

      // Dequeue
      int queueSize = queue.size();
      for (int i = 0; i < queueSize; i++) {
         final Future<?> future = es.submit(() -> {
            assertNotNull(queue.dequeue());
         });

         listOfFutures.add(future);
      }
   }

   private void waitForFutures(ArrayList<Future<?>> listOfFutures) {
      // wait for action
      for (Future<?> future: listOfFutures) {
         try
         {
            future.get();
         } catch (InterruptedException | ExecutionException e)
         {
            e.printStackTrace();
            assert (false);
         }
      }
   }
}
