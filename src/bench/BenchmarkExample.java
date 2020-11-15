package bench;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
public class BenchmarkExample
{
   @Param({
         "1",
         "2"
   })
   public String testValue;

   @Setup
   public void setup() {
      // bench setup goes here
      System.out.println("Setting up: " + testValue);
   }

   @Benchmark
   public void benchMarkFunction() {
      // code to benchmark goes here
      System.out.println("Benchmarking " + testValue);
   }
}
