package bench;

import Interfaces.IPriorityQueue;
import Utils.UtilityFunctions;
import org.junit.Assert;
import queues.CoarseLockBasedPriorityQueue;
import queues.FineLockBasedPriorityQueue;
import queues.JavaLibPriorityQueue;
import queues.LockFreePriorityQueue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

class TestUtils
{
   public static void main(String args[]) {
      // Test reading a file.
      final int[] array = UtilityFunctions.readFromFile(
            "src/bench/benchCases/RandomOrder_1000000.txt"
      );

      final IPriorityQueue<Integer> fineQueue = new FineLockBasedPriorityQueue<>();
      final IPriorityQueue<Integer> coarseQueue = new CoarseLockBasedPriorityQueue<>();
      final IPriorityQueue<Integer> javaQueue = new JavaLibPriorityQueue<>();
      final IPriorityQueue<Integer> lockFreeQueue = new LockFreePriorityQueue<>();
      ExecutorService executorService = Executors.newFixedThreadPool(16);
      /*
      Arrays.stream(array).forEach(i -> {
         fineQueue.enqueue(i);
         javaQueue.enqueue(i);
         lockFreeQueue.enqueue(i);
      });
       */


      final Long startTime = System.currentTimeMillis();
      performAddAndRemoveTestCaseAction(executorService, coarseQueue, array);
      // performRemoveTestCaseAction(executorService, javaQueue);
      System.out.println("Duration In Ms: " + (System.currentTimeMillis() - startTime));
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

   static void performAddAndRemoveSimultaneouslyTestCaseAction(
         ExecutorService executorService,
         IPriorityQueue<Integer> queue,
         int[] inputValues
   ) {
      Assert.assertNotEquals(0, queue.size());
      Assert.assertNotEquals(0, inputValues.length);
      final ArrayList<Future<?>> listOfFutures = new ArrayList<>();

      final int expectedFinalSize = queue.size();
      final int inputValuesSize = inputValues.length;

      // Enqueue
      for (Integer value: inputValues) {
         final Future<?> future = executorService.submit(() -> {
            Assert.assertTrue(queue.enqueue(value));
         });

         listOfFutures.add(future);
      }

      // Dequeue
      // Note: since we will be passing a populated queue initially
      // with the same save of input array, dequeue should never be null.
      for (int i = 0; i < inputValuesSize; i++) {
         final Future<?> future = executorService.submit(() -> {
            Assert.assertNotNull(queue.dequeue());
         });

         listOfFutures.add(future);
      }

      waitForFutures(listOfFutures);

      Assert.assertEquals(expectedFinalSize, queue.size());
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
