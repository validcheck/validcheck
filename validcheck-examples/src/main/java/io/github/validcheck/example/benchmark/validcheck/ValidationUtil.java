package io.github.validcheck.example.benchmark.validcheck;

import io.github.validcheck.Check;
import io.github.validcheck.ConfiguredCheck;
import io.github.validcheck.ValidationConfig;

/** Utility class with optimized ValidCheck configuration for benchmarking. */
public final class ValidationUtil {

  /**
   * Optimized configuration for benchmarking: - fillStackTrace = false (better performance) -
   * includeActualValue = true (helpful error messages) - actualValueMaxLength = 128 (reasonable
   * limit)
   */
  private static final ValidationConfig BENCHMARK_CONFIG = new ValidationConfig(false, true, 128);

  /** Configured check instance with optimized settings for benchmarking. */
  public static final ConfiguredCheck CHECK = Check.withConfig(BENCHMARK_CONFIG);

  private ValidationUtil() {
    // Utility class - no instantiation
  }
}
