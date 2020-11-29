package bench;

import Interfaces.IPriorityQueue;
import Utils.UtilityFunctions;
import org.openjdk.jmh.annotations.*;
import queues.CoarseLockBasedPriorityQueue;
import queues.JavaLibPriorityQueue;
import queues.FineLockBasedPriorityQueue;
import queues.LockFreePriorityQueue;

import java.util.Arrays;
import java.util.concurrent.*;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
public class BenchMarkBestCase
{
   @Param({
         TestCasesBasePath + "AscendingOrder_1000.txt",
         TestCasesBasePath + "AscendingOrder_10000.txt",
         TestCasesBasePath + "AscendingOrder_100000.txt",
         TestCasesBasePath + "AscendingOrder_1000000.txt"
   })
   public String fileUnderTest;

   // Initialize Queue here to avoid construction time
   // impacting performance
   final IPriorityQueue<Integer> javaLibPriorityQueue = new JavaLibPriorityQueue<>();
   final IPriorityQueue<Integer> lockFreePriorityQueue = new LockFreePriorityQueue<>();
   final IPriorityQueue<Integer> fineLockBasedPriorityQueue = new FineLockBasedPriorityQueue<>();
   final IPriorityQueue<Integer> coarseLockBasedPriorityQueue = new CoarseLockBasedPriorityQueue<>();

   final IPriorityQueue<Integer> populatedJavaLibPriorityQueue = new JavaLibPriorityQueue<>();
   final IPriorityQueue<Integer> populatedLockFreePriorityQueue = new LockFreePriorityQueue<>();
   final IPriorityQueue<Integer> populatedFineLockBasedPriorityQueue = new FineLockBasedPriorityQueue<>();
   final IPriorityQueue<Integer> populatedCoarseLockBasedPriorityQueue = new CoarseLockBasedPriorityQueue<>();

   ExecutorService executorService_1Threads;
   ExecutorService executorService_4Threads;
   ExecutorService executorService_8Threads;
   ExecutorService executorService_16Threads;
   ExecutorService executorService_32Threads;
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
      executorService_32Threads = Executors.newFixedThreadPool(32);
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
      executorService_32Threads.shutdownNow();
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
      fineLockBasedPriorityQueue.clear();
      coarseLockBasedPriorityQueue.clear();

      populatedJavaLibPriorityQueue.clear();
      populatedLockFreePriorityQueue.clear();
      populatedFineLockBasedPriorityQueue.clear();
      populatedCoarseLockBasedPriorityQueue.clear();
   }

   private void populateQueues(int[] array) {
      // Populate queues to test dequeue only case
      Arrays.stream(array).forEach(i -> {
         populatedJavaLibPriorityQueue.enqueue(i);
         populatedLockFreePriorityQueue.enqueue(i);
         populatedFineLockBasedPriorityQueue.enqueue(i);
         populatedCoarseLockBasedPriorityQueue.enqueue(i);
      });
   }

   @Benchmark
   public void benchJavaLibPriorityQueue_1Thread_EnqThenDeq() {
      TestUtils.performAddAndRemoveTestCaseAction(executorService_1Threads, javaLibPriorityQueue, array);
   }

   @Benchmark
   public void benchJavaLibPriorityQueue_4Threads_EnqThenDeq() {
      TestUtils.performAddAndRemoveTestCaseAction(executorService_4Threads, javaLibPriorityQueue, array);
   }

   @Benchmark
   public void benchJavaLibPriorityQueue_8Threads_EnqThenDeq() {
      TestUtils.performAddAndRemoveTestCaseAction(executorService_8Threads, javaLibPriorityQueue, array);
   }

   @Benchmark
   public void benchJavaLibPriorityQueue_16Threads_EnqThenDeq() {
      TestUtils.performAddAndRemoveTestCaseAction(executorService_16Threads, javaLibPriorityQueue, array);
   }

   @Benchmark
   public void benchJavaLibPriorityQueue_32Threads_EnqThenDeq() {
      TestUtils.performAddAndRemoveTestCaseAction(executorService_32Threads, javaLibPriorityQueue, array);
   }

   @Benchmark
   public void benchLockFreePriorityQueue_1Thread_EnqThenDeq() {
      TestUtils.performAddAndRemoveTestCaseAction(executorService_1Threads, lockFreePriorityQueue, array);
   }

   @Benchmark
   public void benchLockFreePriorityQueue_4Threads_EnqThenDeq() {
      TestUtils.performAddAndRemoveTestCaseAction(executorService_4Threads, lockFreePriorityQueue, array);
   }

   @Benchmark
   public void benchLockFreePriorityQueue_8Threads_EnqThenDeq() {
      TestUtils.performAddAndRemoveTestCaseAction(executorService_8Threads, lockFreePriorityQueue, array);
   }

   @Benchmark
   public void benchLockFreePriorityQueue_16Threads_EnqThenDeq() {
      TestUtils.performAddAndRemoveTestCaseAction(executorService_16Threads, lockFreePriorityQueue, array);
   }

   @Benchmark
   public void benchLockFreePriorityQueue_32Threads_EnqThenDeq() {
      TestUtils.performAddAndRemoveTestCaseAction(executorService_32Threads, lockFreePriorityQueue, array);
   }

   @Benchmark
   public void benchFineLockBasedPriorityQueue_1Thread_EnqThenDeq() {
      TestUtils.performAddAndRemoveTestCaseAction(executorService_1Threads, fineLockBasedPriorityQueue, array);
   }

   @Benchmark
   public void benchFineLockBasedPriorityQueue_4Threads_EnqThenDeq() {
      TestUtils.performAddAndRemoveTestCaseAction(executorService_4Threads, fineLockBasedPriorityQueue, array);
   }

   @Benchmark
   public void benchFineLockBasedPriorityQueue_8Threads_EnqThenDeq() {
      TestUtils.performAddAndRemoveTestCaseAction(executorService_8Threads, fineLockBasedPriorityQueue, array);
   }

   @Benchmark
   public void benchFineLockBasedPriorityQueue_16Threads_EnqThenDeq() {
      TestUtils.performAddAndRemoveTestCaseAction(executorService_16Threads, fineLockBasedPriorityQueue, array);
   }

   @Benchmark
   public void benchFineLockBasedPriorityQueue_32Threads_EnqThenDeq() {
      TestUtils.performAddAndRemoveTestCaseAction(executorService_32Threads, fineLockBasedPriorityQueue, array);
   }

   @Benchmark
   public void benchCoarseLockBasedPriorityQueue_1Thread_EnqThenDeq() {
      TestUtils.performAddAndRemoveTestCaseAction(executorService_1Threads, coarseLockBasedPriorityQueue, array);
   }

   @Benchmark
   public void benchCoarseLockBasedPriorityQueue_4Threads_EnqThenDeq() {
      TestUtils.performAddAndRemoveTestCaseAction(executorService_4Threads, coarseLockBasedPriorityQueue, array);
   }

   @Benchmark
   public void benchCoarseLockBasedPriorityQueue_8Threads_EnqThenDeq() {
      TestUtils.performAddAndRemoveTestCaseAction(executorService_8Threads, coarseLockBasedPriorityQueue, array);
   }

   @Benchmark
   public void benchCoarseLockBasedPriorityQueue_16Threads_EnqThenDeq() {
      TestUtils.performAddAndRemoveTestCaseAction(executorService_16Threads, coarseLockBasedPriorityQueue, array);
   }

   @Benchmark
   public void benchCoarseLockBasedPriorityQueue_32Threads_EnqThenDeq() {
      TestUtils.performAddAndRemoveTestCaseAction(executorService_32Threads, coarseLockBasedPriorityQueue, array);
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
   public void benchJavaLibPriorityQueue_32Threads_EnqOnly() {
      TestUtils.performAddTestCaseAction(executorService_32Threads, javaLibPriorityQueue, array);
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
   public void benchLockFreePriorityQueue_32Threads_EnqOnly() {
      TestUtils.performAddTestCaseAction(executorService_32Threads, lockFreePriorityQueue, array);
   }

   @Benchmark
   public void benchFineLockBasedPriorityQueue_1Thread_EnqOnly() {
      TestUtils.performAddTestCaseAction(executorService_1Threads, fineLockBasedPriorityQueue, array);
   }

   @Benchmark
   public void benchFineLockBasedPriorityQueue_4Threads_EnqOnly() {
      TestUtils.performAddTestCaseAction(executorService_4Threads, fineLockBasedPriorityQueue, array);
   }

   @Benchmark
   public void benchFineLockBasedPriorityQueue_8Threads_EnqOnly() {
      TestUtils.performAddTestCaseAction(executorService_8Threads, fineLockBasedPriorityQueue, array);
   }

   @Benchmark
   public void benchFineLockBasedPriorityQueue_16Threads_EnqOnly() {
      TestUtils.performAddTestCaseAction(executorService_16Threads, fineLockBasedPriorityQueue, array);
   }

   @Benchmark
   public void benchFineLockBasedPriorityQueue_32Threads_EnqOnly() {
      TestUtils.performAddTestCaseAction(executorService_32Threads, fineLockBasedPriorityQueue, array);
   }

   @Benchmark
   public void benchCoarseLockBasedPriorityQueue_1Thread_EnqOnly() {
      TestUtils.performAddTestCaseAction(executorService_1Threads, coarseLockBasedPriorityQueue, array);
   }

   @Benchmark
   public void benchCoarseLockBasedPriorityQueue_4Threads_EnqOnly() {
      TestUtils.performAddTestCaseAction(executorService_4Threads, coarseLockBasedPriorityQueue, array);
   }

   @Benchmark
   public void benchCoarseLockBasedPriorityQueue_8Threads_EnqOnly() {
      TestUtils.performAddTestCaseAction(executorService_8Threads, coarseLockBasedPriorityQueue, array);
   }

   @Benchmark
   public void benchCoarseLockBasedPriorityQueue_16Threads_EnqOnly() {
      TestUtils.performAddTestCaseAction(executorService_16Threads, coarseLockBasedPriorityQueue, array);
   }

   @Benchmark
   public void benchCoarseLockBasedPriorityQueue_32Threads_EnqOnly() {
      TestUtils.performAddTestCaseAction(executorService_32Threads, coarseLockBasedPriorityQueue, array);
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
   public void benchJavaLibPriorityQueue_32Threads_DeqOnly() {
      TestUtils.performRemoveTestCaseAction(executorService_32Threads, populatedJavaLibPriorityQueue);
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
   public void benchLockFreePriorityQueue_32Threads_DeqOnly() {
      TestUtils.performRemoveTestCaseAction(executorService_32Threads, populatedLockFreePriorityQueue);
   }

   @Benchmark
   public void benchFineLockBasedPriorityQueue_1Thread_DeqOnly() {
      TestUtils.performRemoveTestCaseAction(executorService_1Threads, populatedFineLockBasedPriorityQueue);
   }

   @Benchmark
   public void benchFineLockBasedPriorityQueue_4Threads_DeqOnly() {
      TestUtils.performRemoveTestCaseAction(executorService_4Threads, populatedFineLockBasedPriorityQueue);
   }

   @Benchmark
   public void benchFineLockBasedPriorityQueue_8Threads_DeqOnly() {
      TestUtils.performRemoveTestCaseAction(executorService_8Threads, populatedFineLockBasedPriorityQueue);
   }

   @Benchmark
   public void benchFineLockBasedPriorityQueue_16Threads_DeqOnly() {
      TestUtils.performRemoveTestCaseAction(executorService_16Threads, populatedFineLockBasedPriorityQueue);
   }

   @Benchmark
   public void benchFineLockBasedPriorityQueue_32Threads_DeqOnly() {
      TestUtils.performRemoveTestCaseAction(executorService_32Threads, populatedFineLockBasedPriorityQueue);
   }

   @Benchmark
   public void benchCoarseLockBasedPriorityQueue_1Thread_DeqOnly() {
      TestUtils.performRemoveTestCaseAction(executorService_1Threads, populatedCoarseLockBasedPriorityQueue);
   }

   @Benchmark
   public void benchCoarseLockBasedPriorityQueue_4Threads_DeqOnly() {
      TestUtils.performRemoveTestCaseAction(executorService_4Threads, populatedCoarseLockBasedPriorityQueue);
   }

   @Benchmark
   public void benchCoarseLockBasedPriorityQueue_8Threads_DeqOnly() {
      TestUtils.performRemoveTestCaseAction(executorService_8Threads, populatedCoarseLockBasedPriorityQueue);
   }

   @Benchmark
   public void benchCoarseLockBasedPriorityQueue_16Threads_DeqOnly() {
      TestUtils.performRemoveTestCaseAction(executorService_16Threads, populatedCoarseLockBasedPriorityQueue);
   }

   @Benchmark
   public void benchCoarseLockBasedPriorityQueue_32Threads_DeqOnly() {
      TestUtils.performRemoveTestCaseAction(executorService_32Threads, populatedCoarseLockBasedPriorityQueue);
   }

   // Enqueue and Dequeue Simultaneously

   @Benchmark
   public void benchJavaLibPriorityQueue_1Thread_EnqAndDeqSimultaneously() {
      TestUtils.performAddAndRemoveSimultaneouslyTestCaseAction(executorService_1Threads, populatedJavaLibPriorityQueue, array);
   }

   @Benchmark
   public void benchJavaLibPriorityQueue_4Threads_EnqAndDeqSimultaneously() {
      TestUtils.performAddAndRemoveSimultaneouslyTestCaseAction(executorService_4Threads, populatedJavaLibPriorityQueue, array);
   }

   @Benchmark
   public void benchJavaLibPriorityQueue_8Threads_EnqAndDeqSimultaneously() {
      TestUtils.performAddAndRemoveSimultaneouslyTestCaseAction(executorService_8Threads, populatedJavaLibPriorityQueue, array);
   }

   @Benchmark
   public void benchJavaLibPriorityQueue_16Threads_EnqAndDeqSimultaneously() {
      TestUtils.performAddAndRemoveSimultaneouslyTestCaseAction(executorService_16Threads, populatedJavaLibPriorityQueue, array);
   }

   @Benchmark
   public void benchJavaLibPriorityQueue_32Threads_EnqAndDeqSimultaneously() {
      TestUtils.performAddAndRemoveSimultaneouslyTestCaseAction(executorService_32Threads, populatedJavaLibPriorityQueue, array);
   }

   @Benchmark
   public void benchLockFreePriorityQueue_1Thread_EnqAndDeqSimultaneously() {
      TestUtils.performAddAndRemoveSimultaneouslyTestCaseAction(executorService_1Threads, populatedLockFreePriorityQueue, array);
   }

   @Benchmark
   public void benchLockFreePriorityQueue_4Threads_EnqAndDeqSimultaneously() {
      TestUtils.performAddAndRemoveSimultaneouslyTestCaseAction(executorService_4Threads, populatedLockFreePriorityQueue, array);
   }

   @Benchmark
   public void benchLockFreePriorityQueue_8Threads_EnqAndDeqSimultaneously() {
      TestUtils.performAddAndRemoveSimultaneouslyTestCaseAction(executorService_8Threads, populatedLockFreePriorityQueue, array);
   }

   @Benchmark
   public void benchLockFreePriorityQueue_16Threads_EnqAndDeqSimultaneously() {
      TestUtils.performAddAndRemoveSimultaneouslyTestCaseAction(executorService_16Threads, populatedLockFreePriorityQueue, array);
   }

   @Benchmark
   public void benchLockFreePriorityQueue_32Threads_EnqAndDeqSimultaneously() {
      TestUtils.performAddAndRemoveSimultaneouslyTestCaseAction(executorService_32Threads, populatedLockFreePriorityQueue, array);
   }

   @Benchmark
   public void benchFineLockBasedPriorityQueue_1Thread_EnqAndDeqSimultaneously() {
      TestUtils.performAddAndRemoveSimultaneouslyTestCaseAction(executorService_1Threads, populatedFineLockBasedPriorityQueue, array);
   }

   @Benchmark
   public void benchFineLockBasedPriorityQueue_4Threads_EnqAndDeqSimultaneously() {
      TestUtils.performAddAndRemoveSimultaneouslyTestCaseAction(executorService_4Threads, populatedFineLockBasedPriorityQueue, array);
   }

   @Benchmark
   public void benchFineLockBasedPriorityQueue_8Threads_EnqAndDeqSimultaneously() {
      TestUtils.performAddAndRemoveSimultaneouslyTestCaseAction(executorService_8Threads, populatedFineLockBasedPriorityQueue, array);
   }

   @Benchmark
   public void benchFineLockBasedPriorityQueue_16Threads_EnqAndDeqSimultaneously() {
      TestUtils.performAddAndRemoveSimultaneouslyTestCaseAction(executorService_16Threads, populatedFineLockBasedPriorityQueue, array);
   }

   @Benchmark
   public void benchFineLockBasedPriorityQueue_32Threads_EnqAndDeqSimultaneously() {
      TestUtils.performAddAndRemoveSimultaneouslyTestCaseAction(executorService_32Threads, populatedFineLockBasedPriorityQueue, array);
   }

   @Benchmark
   public void benchCoarseLockBasedPriorityQueue_1Thread_EnqAndDeqSimultaneously() {
      TestUtils.performAddAndRemoveSimultaneouslyTestCaseAction(executorService_1Threads, populatedCoarseLockBasedPriorityQueue, array);
   }

   @Benchmark
   public void benchCoarseLockBasedPriorityQueue_4Threads_EnqAndDeqSimultaneously() {
      TestUtils.performAddAndRemoveSimultaneouslyTestCaseAction(executorService_4Threads, populatedCoarseLockBasedPriorityQueue, array);
   }

   @Benchmark
   public void benchCoarseLockBasedPriorityQueue_8Threads_EnqAndDeqSimultaneously() {
      TestUtils.performAddAndRemoveSimultaneouslyTestCaseAction(executorService_8Threads, populatedCoarseLockBasedPriorityQueue, array);
   }

   @Benchmark
   public void benchCoarseLockBasedPriorityQueue_16Threads_EnqAndDeqSimultaneously() {
      TestUtils.performAddAndRemoveSimultaneouslyTestCaseAction(executorService_16Threads, populatedCoarseLockBasedPriorityQueue, array);
   }

   @Benchmark
   public void benchCoarseLockBasedPriorityQueue_32Threads_EnqAndDeqSimultaneously() {
      TestUtils.performAddAndRemoveSimultaneouslyTestCaseAction(executorService_32Threads, populatedCoarseLockBasedPriorityQueue, array);
   }
}
