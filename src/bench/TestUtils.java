package bench;

import Interfaces.IPriorityQueue;
import Utils.UtilityFunctions;
import org.junit.Assert;
import queues.JavaLibPriorityQueue;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

class TestUtils
{
   public static void main(String args[]) {
      // Test reading a file.
      final int[] array = UtilityFunctions.readFromFile(
            "src/bench/benchCases/AscendingOrder_1000.txt"
      );

      final IPriorityQueue<Integer> javaLibPriorityQueue = new JavaLibPriorityQueue<>();
      ExecutorService executorService_4Threads = Executors.newFixedThreadPool(4);

      performAddTestCaseAction(executorService_4Threads, javaLibPriorityQueue, array);
   }

   static void performAddAndRemoveTestCaseAction(
         ExecutorService executorService,
         IPriorityQueue<Integer> queue,
         int[] inputValues
   ) {
      Assert.assertEquals(0, queue.size());
      Assert.assertNotEquals(0, inputValues.length);
      final ArrayList<Future<?>> listOfFutures = new ArrayList<>();

      // Enqueue
      for (Integer value: inputValues) {
         final Future<?> future = executorService.submit(() -> {
            Assert.assertTrue(queue.enqueue(value));
         });

         listOfFutures.add(future);
      }

      waitForFutures(listOfFutures);

      Assert.assertEquals(queue.size(), listOfFutures.size());

      // Clear futures
      listOfFutures.clear();

      // Dequeue
      final int queueSize = queue.size();
      for (int i = 0; i < queueSize; i++) {
         final Future<?> future = executorService.submit(() -> {
            Assert.assertNotNull(queue.dequeue());
         });

         listOfFutures.add(future);
      }

      waitForFutures(listOfFutures);
   }

   static void performAddTestCaseAction(
         ExecutorService executorService,
         IPriorityQueue<Integer> queue,
         int[] inputValues
   ) {
      Assert.assertEquals(0, queue.size());
      Assert.assertNotEquals(0, inputValues.length);
      final ArrayList<Future<?>> listOfFutures = new ArrayList<>();

      // Enqueue
      for (Integer value: inputValues) {
         final Future<?> future = executorService.submit(() -> {
            Assert.assertTrue(queue.enqueue(value));
         });

         listOfFutures.add(future);
      }

      waitForFutures(listOfFutures);

      Assert.assertEquals(queue.size(), listOfFutures.size());
   }

   static void performRemoveTestCaseAction(
         ExecutorService executorService,
         IPriorityQueue<Integer> queue
   ) {
      Assert.assertNotEquals(0, queue.size());
      final ArrayList<Future<?>> listOfFutures = new ArrayList<>();
      // Dequeue
      final int queueSize = queue.size();
      for (int i = 0; i < queueSize; i++) {
         final Future<?> future = executorService.submit(() -> {
            Assert.assertNotNull(queue.dequeue());
         });

         listOfFutures.add(future);
      }

      waitForFutures(listOfFutures);
   }

   private static void waitForFutures(ArrayList<Future<?>> listOfFutures) {
      // wait for action
      listOfFutures.stream().forEach(f -> {
         try
         {
            f.get();
         } catch (InterruptedException | ExecutionException e)
         {
            e.printStackTrace();
         }
      });
   }
}
