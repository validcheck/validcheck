package io.github.validcheck.example.benchmark;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * Runner for the validation benchmark.
 *
 * <p>Run this class to execute the JMH benchmark comparing ValidCheck vs Bean Validation.
 */
public class BenchmarkRunner {

  public static void main(String[] args) throws RunnerException {
    Options opt =
        new OptionsBuilder().include(ValidationBenchmark.class.getSimpleName()).forks(1).build();

    new Runner(opt).run();
  }
}
