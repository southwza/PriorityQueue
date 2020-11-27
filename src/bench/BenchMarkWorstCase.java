package bench;

import Interfaces.IPriorityQueue;
import Utils.UtilityFunctions;
import org.openjdk.jmh.annotations.*;
import queues.JavaLibPriorityQueue;
import queues.LockBasedPriorityQueue;
import queues.LockFreePriorityQueue;

import java.util.Arrays;
import java.util.concurrent.*;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
public class BenchMarkWorstCase
{
   @Param({
         TestCasesBasePath + "DescendingOrder_1000.txt",
         TestCasesBasePath + "DescendingOrder_10000.txt",
         TestCasesBasePath + "DescendingOrder_100000.txt",
         TestCasesBasePath + "DescendingOrder_1000000.txt"
   })
   public String fileUnderTest;

   // Initialize Queue here to avoid construction time
   // impacting performance
   final IPriorityQueue<Integer> javaLibPriorityQueue = new JavaLibPriorityQueue<>();
   final IPriorityQueue<Integer> lockFreePriorityQueue = new LockFreePriorityQueue<>();
   final IPriorityQueue<Integer> lockBasedPriorityQueue = new LockBasedPriorityQueue<>();

   final IPriorityQueue<Integer> populatedJavaLibPriorityQueue = new JavaLibPriorityQueue<>();
   final IPriorityQueue<Integer> populatedLockFreePriorityQueue = new LockFreePriorityQueue<>();
   final IPriorityQueue<Integer> populatedLockBasedPriorityQueue = new LockBasedPriorityQueue<>();

   ExecutorService executorService_1Threads;
   ExecutorService executorService_4Threads;
   ExecutorService executorService_8Threads;
   ExecutorService executorService_16Threads;
   private final String TestCasesBasePath = "src/bench/benchCases/";
   private int[] array;

   // Per Trial Setup
   @Setup(Level.Trial)
   public void setup() {
      System.out.println();
      // System.out.println("Setup was called.");
      System.out.println("System Cores: " + Runtime.getRuntime().availableProcessors());
      System.out.println("Benchmarking " + fileUnderTest);
      array = UtilityFunctions.readFromFile(fileUnderTest);

      // This is needed since Teardown will shut them down.
      executorService_1Threads = Executors.newFixedThreadPool(1);
      executorService_4Threads = Executors.newFixedThreadPool(4);
      executorService_8Threads = Executors.newFixedThreadPool(8);
      executorService_16Threads = Executors.newFixedThreadPool(16);
   }

   // Per Trial TearDown
   @TearDown(Level.Trial)
   public void tearDown() {
      // System.out.println();
      // System.out.println("TearDown was called.");
      executorService_1Threads.shutdownNow();
      executorService_4Threads.shutdownNow();
      executorService_8Threads.shutdownNow();
      executorService_16Threads.shutdownNow();
   }

   // Per invocation Setup
   @Setup(Level.Invocation)
   public void setupInvocation() {
      // System.out.println();
      // System.out.println("Setup Invocation was called.");

      populateQueues(array);
   }

   // Per invocation TearDown
   @TearDown(Level.Invocation)
   public void tearDownInvocation() {
      // System.out.println();
      // System.out.println("TearDown Invocation was called.");

      clearQueues();
   }

   private void clearQueues() {
      javaLibPriorityQueue.clear();
      lockFreePriorityQueue.clear();
      lockBasedPriorityQueue.clear();

      populatedJavaLibPriorityQueue.clear();
      populatedLockFreePriorityQueue.clear();
      populatedLockBasedPriorityQueue.clear();
   }

   private void populateQueues(int[] array) {
      // Populate queues to test dequeue only case
      Arrays.stream(array).forEach(i -> {
         populatedJavaLibPriorityQueue.enqueue(i);
         populatedLockFreePriorityQueue.enqueue(i);
         populatedLockBasedPriorityQueue.enqueue(i);
      });
   }

   @Benchmark
   public void benchJavaLibPriorityQueue_1Thread_EnqAndDeq() {
      TestUtils.performAddAndRemoveTestCaseAction(executorService_1Threads, javaLibPriorityQueue, array);
   }

   @Benchmark
   public void benchJavaLibPriorityQueue_4Threads_EnqAndDeq() {
      TestUtils.performAddAndRemoveTestCaseAction(executorService_4Threads, javaLibPriorityQueue, array);
   }

   @Benchmark
   public void benchJavaLibPriorityQueue_8Threads_EnqAndDeq() {
      TestUtils.performAddAndRemoveTestCaseAction(executorService_8Threads, javaLibPriorityQueue, array);
   }

   @Benchmark
   public void benchJavaLibPriorityQueue_16Threads_EnqAndDeq() {
      TestUtils.performAddAndRemoveTestCaseAction(executorService_16Threads, javaLibPriorityQueue, array);
   }

   @Benchmark
   public void benchLockFreePriorityQueue_1Thread_EnqAndDeq() {
      TestUtils.performAddAndRemoveTestCaseAction(executorService_1Threads, lockFreePriorityQueue, array);
   }

   @Benchmark
   public void benchLockFreePriorityQueue_4Threads_EnqAndDeq() {
      TestUtils.performAddAndRemoveTestCaseAction(executorService_4Threads, lockFreePriorityQueue, array);
   }

   @Benchmark
   public void benchLockFreePriorityQueue_8Threads_EnqAndDeq() {
      TestUtils.performAddAndRemoveTestCaseAction(executorService_8Threads, lockFreePriorityQueue, array);
   }

   @Benchmark
   public void benchLockFreePriorityQueue_16Threads_EnqAndDeq() {
      TestUtils.performAddAndRemoveTestCaseAction(executorService_16Threads, lockFreePriorityQueue, array);
   }

   @Benchmark
   public void benchLockBasedPriorityQueue_1Thread_EnqAndDeq() {
      TestUtils.performAddAndRemoveTestCaseAction(executorService_1Threads, lockBasedPriorityQueue, array);
   }

   @Benchmark
   public void benchLockBasedPriorityQueue_4Threads_EnqAndDeq() {
      TestUtils.performAddAndRemoveTestCaseAction(executorService_4Threads, lockBasedPriorityQueue, array);
   }

   @Benchmark
   public void benchLockBasedPriorityQueue_8Threads_EnqAndDeq() {
      TestUtils.performAddAndRemoveTestCaseAction(executorService_8Threads, lockBasedPriorityQueue, array);
   }

   @Benchmark
   public void benchLockBasedPriorityQueue_16Threads_EnqAndDeq() {
      TestUtils.performAddAndRemoveTestCaseAction(executorService_16Threads, lockBasedPriorityQueue, array);
   }

   // Only adding elements

   @Benchmark
   public void benchJavaLibPriorityQueue_1Thread_EnqOnly() {
      TestUtils.performAddTestCaseAction(executorService_1Threads, javaLibPriorityQueue, array);
   }

   @Benchmark
   public void benchJavaLibPriorityQueue_4Threads_EnqOnly() {
      TestUtils.performAddTestCaseAction(executorService_4Threads, javaLibPriorityQueue, array);
   }

   @Benchmark
   public void benchJavaLibPriorityQueue_8Threads_EnqOnly() {
      TestUtils.performAddTestCaseAction(executorService_8Threads, javaLibPriorityQueue, array);
   }

   @Benchmark
   public void benchJavaLibPriorityQueue_16Threads_EnqOnly() {
      TestUtils.performAddTestCaseAction(executorService_16Threads, javaLibPriorityQueue, array);
   }

   @Benchmark
   public void benchLockFreePriorityQueue_1Thread_EnqOnly() {
      TestUtils.performAddTestCaseAction(executorService_1Threads, lockFreePriorityQueue, array);
   }

   @Benchmark
   public void benchLockFreePriorityQueue_4Threads_EnqOnly() {
      TestUtils.performAddTestCaseAction(executorService_4Threads, lockFreePriorityQueue, array);
   }

   @Benchmark
   public void benchLockFreePriorityQueue_8Threads_EnqOnly() {
      TestUtils.performAddTestCaseAction(executorService_8Threads, lockFreePriorityQueue, array);
   }

   @Benchmark
   public void benchLockFreePriorityQueue_16Threads_EnqOnly() {
      TestUtils.performAddTestCaseAction(executorService_16Threads, lockFreePriorityQueue, array);
   }

   @Benchmark
   public void benchLockBasedPriorityQueue_1Thread_EnqOnly() {
      TestUtils.performAddTestCaseAction(executorService_1Threads, lockBasedPriorityQueue, array);
   }

   @Benchmark
   public void benchLockBasedPriorityQueue_4Threads_EnqOnly() {
      TestUtils.performAddTestCaseAction(executorService_4Threads, lockBasedPriorityQueue, array);
   }

   @Benchmark
   public void benchLockBasedPriorityQueue_8Threads_EnqOnly() {
      TestUtils.performAddTestCaseAction(executorService_8Threads, lockBasedPriorityQueue, array);
   }

   @Benchmark
   public void benchLockBasedPriorityQueue_16Threads_EnqOnly() {
      TestUtils.performAddTestCaseAction(executorService_16Threads, lockBasedPriorityQueue, array);
   }

   // Only removing elements

   @Benchmark
   public void benchJavaLibPriorityQueue_1Thread_DeqOnly() {
      TestUtils.performRemoveTestCaseAction(executorService_1Threads, populatedJavaLibPriorityQueue);
   }

   @Benchmark
   public void benchJavaLibPriorityQueue_4Threads_DeqOnly() {
      TestUtils.performRemoveTestCaseAction(executorService_4Threads, populatedJavaLibPriorityQueue);
   }

   @Benchmark
   public void benchJavaLibPriorityQueue_8Threads_DeqOnly() {
      TestUtils.performRemoveTestCaseAction(executorService_8Threads, populatedJavaLibPriorityQueue);
   }

   @Benchmark
   public void benchJavaLibPriorityQueue_16Threads_DeqOnly() {
      TestUtils.performRemoveTestCaseAction(executorService_16Threads, populatedJavaLibPriorityQueue);
   }

   @Benchmark
   public void benchLockFreePriorityQueue_1Thread_DeqOnly() {
      TestUtils.performRemoveTestCaseAction(executorService_1Threads, populatedLockFreePriorityQueue);
   }

   @Benchmark
   public void benchLockFreePriorityQueue_4Threads_DeqOnly() {
      TestUtils.performRemoveTestCaseAction(executorService_4Threads, populatedLockFreePriorityQueue);
   }

   @Benchmark
   public void benchLockFreePriorityQueue_8Threads_DeqOnly() {
      TestUtils.performRemoveTestCaseAction(executorService_8Threads, populatedLockFreePriorityQueue);
   }

   @Benchmark
   public void benchLockFreePriorityQueue_16Threads_DeqOnly() {
      TestUtils.performRemoveTestCaseAction(executorService_16Threads, populatedLockFreePriorityQueue);
   }

   @Benchmark
   public void benchLockBasedPriorityQueue_1Thread_DeqOnly() {
      TestUtils.performRemoveTestCaseAction(executorService_1Threads, populatedLockBasedPriorityQueue);
   }

   @Benchmark
   public void benchLockBasedPriorityQueue_4Threads_DeqOnly() {
      TestUtils.performRemoveTestCaseAction(executorService_4Threads, populatedLockBasedPriorityQueue);
   }

   @Benchmark
   public void benchLockBasedPriorityQueue_8Threads_DeqOnly() {
      TestUtils.performRemoveTestCaseAction(executorService_8Threads, populatedLockBasedPriorityQueue);
   }

   @Benchmark
   public void benchLockBasedPriorityQueue_16Threads_DeqOnly() {
      TestUtils.performRemoveTestCaseAction(executorService_16Threads, populatedLockBasedPriorityQueue);
   }
}
