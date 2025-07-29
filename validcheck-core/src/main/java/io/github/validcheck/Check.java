package io.github.validcheck;

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
  private static final ValidationConfig DEFAULT_CONFIG = new ValidationConfig(true, true);
  private static final ValidationContext DEFAULT_CONTEXT = new ValidationContext(DEFAULT_CONFIG);

  public static BatchValidationContext batch() {
    return new BatchValidationContext(DEFAULT_CONFIG);
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

  /**
   * Private constructor to prevent instantiation. This is a utility class with only static methods.
   */
  private Check() {}
}
