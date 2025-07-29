package io.github.validcheck;

import java.util.Collection;

/**
 * Main entry point for validation operations in Java methods, records and other classes.
 *
 * <p>Provides both fail-fast validation (immediate exception on first error) and batch validation
 * (collect all errors before throwing).
 *
 * <p>Usage examples:
 *
 * <pre>{@code
 * // Fail-fast validation
 * Check.check("name", name).notNull().hasText();
 * Check.check("age", age).isPositive().max(120);
 *
 * // With static import
 * check("name", name).notNull().hasText();
 *
 * // Batch validation
 * var validation = batch();
 * validation.check("name", name).notNull().hasText();
 * validation.check("age", age).isPositive().max(120);
 * validation.validate();
 * }</pre>
 *
 * @since 1.0
 */
public final class Check {
  /**
   * Private constructor to prevent instantiation. This is a utility class with only static methods.
   */
  private Check() {}

  private static final ValidationContext DEFAULT_CONTEXT =
      new ValidationContext(ValidationConfig.DEFAULT);

  public static BatchValidationContext batch() {
    return new BatchValidationContext(ValidationConfig.DEFAULT);
  }

  public static ConfiguredCheck withConfig(ValidationConfig config) {
    return new ConfiguredCheck(config);
  }

  public static void isTrue(boolean truth, String message) {
    DEFAULT_CONTEXT.check(truth).satisfies(t -> t, message);
  }

  public static void isFalse(boolean lie, String message) {
    isTrue(!lie, message);
  }

  public static <T> ValueValidator<T> check(String name, T value) {
    return DEFAULT_CONTEXT.check(name, value);
  }

  public static <T> ValueValidator<T> check(T value) {
    return DEFAULT_CONTEXT.check(value);
  }

  public static StringValidator check(String name, String value) {
    return DEFAULT_CONTEXT.check(name, value);
  }

  public static NumericValidator<Integer> check(String name, int value) {
    return DEFAULT_CONTEXT.check(name, value);
  }

  public static NumericValidator<Integer> check(String name, Integer value) {
    return DEFAULT_CONTEXT.check(name, value);
  }

  public static NumericValidator<Long> check(String name, long value) {
    return DEFAULT_CONTEXT.check(name, value);
  }

  public static NumericValidator<Long> check(String name, Long value) {
    return DEFAULT_CONTEXT.check(name, value);
  }

  public static NumericValidator<Double> check(String name, double value) {
    return DEFAULT_CONTEXT.check(name, value);
  }

  public static NumericValidator<Double> check(String name, Double value) {
    return DEFAULT_CONTEXT.check(name, value);
  }

  public static NumericValidator<Float> check(String name, float value) {
    return DEFAULT_CONTEXT.check(name, value);
  }

  public static NumericValidator<Float> check(String name, Float value) {
    return DEFAULT_CONTEXT.check(name, value);
  }

  public static <T extends Number> NumericValidator<T> check(String name, T value) {
    return DEFAULT_CONTEXT.check(name, value);
  }

  public static <T extends Collection<?>> CollectionValidator<T> check(String name, T value) {
    return DEFAULT_CONTEXT.check(name, value);
  }
}
