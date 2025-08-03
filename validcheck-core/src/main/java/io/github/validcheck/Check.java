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

  /**
   * Creates a new batch validation context that collects multiple validation errors before
   * throwing.
   *
   * <p>Use this when you want to collect all validation errors and present them together, rather
   * than stopping at the first error.
   *
   * <p>Example:
   *
   * <pre>{@code
   * var validation = batch();
   * validation.check("name", name).notNullOrEmpty();
   * validation.check("age", age).isPositive().max(120);
   * validation.validate(); // Throws ValidationException with all errors if any failed
   * }</pre>
   *
   * @return a new batch validation context
   * @since 1.0
   */
  public static BatchValidationContext batch() {
    return new BatchValidationContext(ValidationConfig.DEFAULT);
  }

  /**
   * Creates a validation context with custom configuration.
   *
   * <p>Use this to customize validation behavior such as disabling stack traces for better
   * performance or hiding actual values for security.
   *
   * <p>Example:
   *
   * <pre>{@code
   * var config = new ValidationConfig(false, false, 256);
   * var fastCheck = withConfig(config);
   * fastCheck.check("password", password).notNullOrEmpty();
   * }</pre>
   *
   * @param config the validation configuration to use
   * @return a configured validation context
   * @throws NullPointerException if config is null
   * @since 1.0
   */
  public static ConfiguredCheck withConfig(ValidationConfig config) {
    return new ConfiguredCheck(config);
  }

  /**
   * Asserts that a boolean condition is true, throwing a ValidationException if false.
   *
   * <p>This is useful for custom validation logic that doesn't fit the standard validators.
   *
   * <p>Example:
   *
   * <pre>{@code
   * isTrue(user.isActive(), "User must be active");
   * isTrue(order.getItems().size() > 0, "Order must have items");
   * }</pre>
   *
   * @param truth the condition to check
   * @param message the error message if the condition is false
   * @throws ValidationException if the condition is false
   * @since 1.0
   */
  public static void isTrue(boolean truth, String message) {
    DEFAULT_CONTEXT.check(truth).satisfies(t -> t, message);
  }

  /**
   * Asserts that a boolean condition is false, throwing a ValidationException if true.
   *
   * <p>This is useful for custom validation logic that checks negative conditions.
   *
   * <p>Example:
   *
   * <pre>{@code
   * isFalse(user.isBlocked(), "User must not be blocked");
   * isFalse(account.isExpired(), "Account must not be expired");
   * }</pre>
   *
   * @param lie the condition to check (should be false)
   * @param message the error message if the condition is true
   * @throws ValidationException if the condition is true
   * @since 1.0
   */
  public static void isFalse(boolean lie, String message) {
    isTrue(!lie, message);
  }

  /**
   * Immediately throws a ValidationException with the given message.
   *
   * <p>This is useful for custom validation logic or business rule violations that should fail
   * validation.
   *
   * <p>Example:
   *
   * <pre>{@code
   * if (order.getTotal().compareTo(BigDecimal.ZERO) <= 0) {
   *   fail("Order total must be positive");
   * }
   * }</pre>
   *
   * @param message the error message
   * @throws ValidationException always, with the given message
   * @since 1.0
   */
  public static void fail(String message) {
    DEFAULT_CONTEXT.fail(message);
  }

  /**
   * Creates a validator for the given value without a parameter name.
   *
   * <p>Use this for validating values where the parameter name is not important for error messages.
   *
   * <p>Example:
   *
   * <pre>{@code
   * check(someValue).notNull();
   * check(result).satisfies(r -> r.isValid(), "must be valid");
   * }</pre>
   *
   * @param <T> the type of the value
   * @param value the value to validate
   * @return a validator for the value
   * @since 1.0
   */
  public static <T> ValueValidator<T> check(T value) {
    return DEFAULT_CONTEXT.check(value);
  }

  /**
   * Creates a string validator for the given string value without a parameter name.
   *
   * <p>Use this for validating string values with access to string-specific validation methods.
   *
   * <p>Example:
   *
   * <pre>{@code
   * check(email).notNull().isEmail();
   * check(text).notEmpty().maxLength(100);
   * }</pre>
   *
   * @param value the string value to validate
   * @return a string validator for the value
   * @since 1.0
   */
  public static StringValidator check(String value) {
    return DEFAULT_CONTEXT.check(value);
  }

  /**
   * Creates a numeric validator for the given int value without a parameter name.
   *
   * <p>Use this for validating integer values with access to numeric-specific validation methods.
   *
   * <p>Example:
   *
   * <pre>{@code
   * check(age).isPositive().max(120);
   * check(quantity).isNonNegative().min(1);
   * }</pre>
   *
   * @param value the int value to validate
   * @return a numeric validator for the value
   * @since 1.0
   */
  public static NumericValidator<Integer> check(int value) {
    return DEFAULT_CONTEXT.check(value);
  }

  /**
   * Creates a numeric validator for the given Integer value without a parameter name.
   *
   * <p>Use this for validating Integer objects with access to numeric-specific validation methods.
   *
   * <p>Example:
   *
   * <pre>{@code
   * check(count).notNull().isPositive();
   * check(limit).when(limit != null, v -> v.max(1000));
   * }</pre>
   *
   * @param value the Integer value to validate (may be null)
   * @return a numeric validator for the value
   * @since 1.0
   */
  public static NumericValidator<Integer> check(Integer value) {
    return DEFAULT_CONTEXT.check(value);
  }

  /**
   * Creates a numeric validator for the given long value without a parameter name.
   *
   * @param value the long value to validate
   * @return a numeric validator for the value
   * @since 1.0
   */
  public static NumericValidator<Long> check(long value) {
    return DEFAULT_CONTEXT.check(value);
  }

  /**
   * Creates a numeric validator for the given Long value without a parameter name.
   *
   * @param value the Long value to validate (may be null)
   * @return a numeric validator for the value
   * @since 1.0
   */
  public static NumericValidator<Long> check(Long value) {
    return DEFAULT_CONTEXT.check(value);
  }

  /**
   * Creates a numeric validator for the given double value without a parameter name.
   *
   * @param value the double value to validate
   * @return a numeric validator for the value
   * @since 1.0
   */
  public static NumericValidator<Double> check(double value) {
    return DEFAULT_CONTEXT.check(value);
  }

  /**
   * Creates a numeric validator for the given Double value without a parameter name.
   *
   * @param value the Double value to validate (may be null)
   * @return a numeric validator for the value
   * @since 1.0
   */
  public static NumericValidator<Double> check(Double value) {
    return DEFAULT_CONTEXT.check(value);
  }

  /**
   * Creates a numeric validator for the given float value without a parameter name.
   *
   * @param value the float value to validate
   * @return a numeric validator for the value
   * @since 1.0
   */
  public static NumericValidator<Float> check(float value) {
    return DEFAULT_CONTEXT.check(value);
  }

  /**
   * Creates a numeric validator for the given Float value without a parameter name.
   *
   * @param value the Float value to validate (may be null)
   * @return a numeric validator for the value
   * @since 1.0
   */
  public static NumericValidator<Float> check(Float value) {
    return DEFAULT_CONTEXT.check(value);
  }

  /**
   * Creates a numeric validator for the given Number value without a parameter name.
   *
   * <p>Use this for validating any Number subtype (BigDecimal, BigInteger, etc.) with access to
   * numeric-specific validation methods.
   *
   * <p>Example:
   *
   * <pre>{@code
   * check(bigDecimal).notNull().isPositive();
   * check(bigInteger).min(BigInteger.ZERO);
   * }</pre>
   *
   * @param <T> the Number subtype
   * @param value the Number value to validate (may be null)
   * @return a numeric validator for the value
   * @since 1.0
   */
  public static <T extends Number> NumericValidator<T> check(T value) {
    return DEFAULT_CONTEXT.check(value);
  }

  /**
   * Creates a validator for the given named value.
   *
   * <p>The parameter name will be included in error messages to provide better context.
   *
   * <p>Example:
   *
   * <pre>{@code
   * check("userId", userId).notNull();
   * check("config", config).satisfies(c -> c.isValid(), "must be valid");
   * }</pre>
   *
   * @param <T> the type of the value
   * @param name the parameter name for error messages
   * @param value the value to validate
   * @return a validator for the named value
   * @since 1.0
   */
  public static <T> ValueValidator<T> check(String name, T value) {
    return DEFAULT_CONTEXT.check(name, value);
  }

  /**
   * Creates a collection validator for the given Collection value without a parameter name.
   *
   * <p>Use this for validating collections (List, Set, etc.) with access to collection-specific
   * validation methods.
   *
   * <p>Example:
   *
   * <pre>{@code
   * check(items).notNull().notEmpty();
   * check(tags).minSize(1).maxSize(10);
   * }</pre>
   *
   * @param <T> the Collection subtype
   * @param value the Collection value to validate (may be null)
   * @return a collection validator for the value
   * @since 1.0
   */
  public static <E, T extends Collection<E>> CollectionValidator<T> check(T value) {
    return DEFAULT_CONTEXT.check(value);
  }

  /**
   * Creates a string validator for the given named string value.
   *
   * <p>The parameter name will be included in error messages and you get access to string-specific
   * validation methods.
   *
   * <p>Example:
   *
   * <pre>{@code
   * check("email", email).notNull().isEmail();
   * check("name", name).notEmpty().lengthBetween(2, 50);
   * }</pre>
   *
   * @param name the parameter name for error messages
   * @param value the string value to validate
   * @return a string validator for the named value
   * @since 1.0
   */
  public static StringValidator check(String name, String value) {
    return DEFAULT_CONTEXT.check(name, value);
  }

  /**
   * Creates a numeric validator for the given named int value.
   *
   * <p>The parameter name will be included in error messages and you get access to numeric-specific
   * validation methods.
   *
   * <p>Example:
   *
   * <pre>{@code
   * check("age", age).isPositive().max(120);
   * check("quantity", quantity).isNonNegative().min(1);
   * }</pre>
   *
   * @param name the parameter name for error messages
   * @param value the int value to validate
   * @return a numeric validator for the named value
   * @since 1.0
   */
  public static NumericValidator<Integer> check(String name, int value) {
    return DEFAULT_CONTEXT.check(name, value);
  }

  /**
   * Creates a numeric validator for the given named Integer value.
   *
   * @param name the parameter name for error messages
   * @param value the Integer value to validate (may be null)
   * @return a numeric validator for the named value
   * @since 1.0
   */
  public static NumericValidator<Integer> check(String name, Integer value) {
    return DEFAULT_CONTEXT.check(name, value);
  }

  /**
   * Creates a numeric validator for the given named long value.
   *
   * @param name the parameter name for error messages
   * @param value the long value to validate
   * @return a numeric validator for the named value
   * @since 1.0
   */
  public static NumericValidator<Long> check(String name, long value) {
    return DEFAULT_CONTEXT.check(name, value);
  }

  /**
   * Creates a numeric validator for the given named Long value.
   *
   * @param name the parameter name for error messages
   * @param value the Long value to validate (may be null)
   * @return a numeric validator for the named value
   * @since 1.0
   */
  public static NumericValidator<Long> check(String name, Long value) {
    return DEFAULT_CONTEXT.check(name, value);
  }

  /**
   * Creates a numeric validator for the given named double value.
   *
   * @param name the parameter name for error messages
   * @param value the double value to validate
   * @return a numeric validator for the named value
   * @since 1.0
   */
  public static NumericValidator<Double> check(String name, double value) {
    return DEFAULT_CONTEXT.check(name, value);
  }

  /**
   * Creates a numeric validator for the given named Double value.
   *
   * @param name the parameter name for error messages
   * @param value the Double value to validate (may be null)
   * @return a numeric validator for the named value
   * @since 1.0
   */
  public static NumericValidator<Double> check(String name, Double value) {
    return DEFAULT_CONTEXT.check(name, value);
  }

  /**
   * Creates a numeric validator for the given named float value.
   *
   * @param name the parameter name for error messages
   * @param value the float value to validate
   * @return a numeric validator for the named value
   * @since 1.0
   */
  public static NumericValidator<Float> check(String name, float value) {
    return DEFAULT_CONTEXT.check(name, value);
  }

  /**
   * Creates a numeric validator for the given named Float value.
   *
   * @param name the parameter name for error messages
   * @param value the Float value to validate (may be null)
   * @return a numeric validator for the named value
   * @since 1.0
   */
  public static NumericValidator<Float> check(String name, Float value) {
    return DEFAULT_CONTEXT.check(name, value);
  }

  /**
   * Creates a numeric validator for the given named Number value.
   *
   * <p>Use this for validating any Number subtype (BigDecimal, BigInteger, etc.) with the parameter
   * name included in error messages.
   *
   * <p>Example:
   *
   * <pre>{@code
   * check("price", bigDecimal).notNull().isPositive();
   * check("id", bigInteger).min(BigInteger.ONE);
   * }</pre>
   *
   * @param <T> the Number subtype
   * @param name the parameter name for error messages
   * @param value the Number value to validate (may be null)
   * @return a numeric validator for the named value
   * @since 1.0
   */
  public static <T extends Number> NumericValidator<T> check(String name, T value) {
    return DEFAULT_CONTEXT.check(name, value);
  }

  /**
   * Creates a collection validator for the given named Collection value.
   *
   * <p>The parameter name will be included in error messages and you get access to
   * collection-specific validation methods.
   *
   * <p>Example:
   *
   * <pre>{@code
   * check("items", items).notNull().notEmpty();
   * check("tags", tags).minSize(1).maxSize(10);
   * }</pre>
   *
   * @param <T> the Collection subtype
   * @param name the parameter name for error messages
   * @param value the Collection value to validate (may be null)
   * @return a collection validator for the named value
   * @since 1.0
   */
  public static <E, T extends Collection<E>> CollectionValidator<T> check(String name, T value) {
    return DEFAULT_CONTEXT.check(name, value);
  }
}
