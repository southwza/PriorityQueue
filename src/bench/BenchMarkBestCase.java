package bench;

import Interfaces.IPriorityQueue;
import Utils.UtilityFunctions;
import org.openjdk.jmh.annotations.*;
import queues.JavaLibPriorityQueue;

import java.util.concurrent.*;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
public class BenchMarkBestCase
{
   // Note: we cannot instantiate executorService in tests since it will
   // be run multiple times would will allocate multiple executor services.
   ExecutorService executorService_4Threads = Executors.newFixedThreadPool(4);
   ExecutorService executorService_8Threads = Executors.newFixedThreadPool(8);
   ExecutorService executorService_16Threads = Executors.newFixedThreadPool(16);
   private final String TestCasesBasePath = "src/bench/benchCases/";
   private int[] array;
   @Param({
         TestCasesBasePath + "AscendingOrder_1000.txt"
   })
   public String fileUnderTest;

   @Setup
   public void setup() {
      System.out.println("System Cores: " + Runtime.getRuntime().availableProcessors());
      System.out.println("Benchmarking " + fileUnderTest);
      array = UtilityFunctions.readFromFile(fileUnderTest);
   }

   @TearDown
   public void tearDown() {
      executorService_4Threads.shutdownNow();
      executorService_8Threads.shutdownNow();
      executorService_16Threads.shutdownNow();
   }

   @Benchmark
   public void benchJavaLibPriorityQueue_4Threads() {
      final IPriorityQueue<Integer> queue = new JavaLibPriorityQueue<>();
      TestUtils.performTestCaseAction(executorService_4Threads, queue, array);
   }

   @Benchmark
   public void benchJavaLibPriorityQueue_8Threads() {
      final IPriorityQueue<Integer> queue = new JavaLibPriorityQueue<>();
      TestUtils.performTestCaseAction(executorService_8Threads, queue, array);
   }

   @Benchmark
   public void benchJavaLibPriorityQueue_16Threads() {
      final IPriorityQueue<Integer> queue = new JavaLibPriorityQueue<>();
      TestUtils.performTestCaseAction(executorService_16Threads, queue, array);
   }

   /*
   @Benchmark
   public void benchLockBasedPriorityQueue() {
      // TODO: code to benchmark goes here
   }

   @Benchmark
   public void benchLockFreePriorityQueue() {
      // TODO: code to benchmark goes here
   }
   */
}
