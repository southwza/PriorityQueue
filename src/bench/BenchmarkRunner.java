package bench;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

public class BenchmarkRunner {

   public static void main(String[] args) throws RunnerException {

      Options options = new OptionsBuilder()
            .include(BenchMarkBestCase.class.getSimpleName())
            .include(BenchMarkWorstCase.class.getSimpleName())
            .include(BenchMarkRandomCase.class.getSimpleName())
            .include(BenchMarkMixedCase.class.getSimpleName())
            .timeout(TimeValue.minutes(1))
            .warmupIterations(3) // Default is 5
            .warmupTime(TimeValue.seconds(10)) // Default is 10
            .forks(1)
            .build();

      new Runner(options).run();
   }
}