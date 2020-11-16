package bench;

import Interfaces.IPriorityQueue;
import Utils.UtilityFunctions;
import org.junit.Assert;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.Assert.*;

class TestUtils
{
   public static void main(String args[]) {
      // Test reading a file.
      final int[] array = UtilityFunctions.readFromFile(
            "src/bench/benchCases/AscendingOrder_1000.txt"
      );
   }

   static void performTestCaseAction(
         ExecutorService executorService,
         IPriorityQueue<Integer> queue,
         int[] inputValues
   ) {
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
      int queueSize = queue.size();
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
