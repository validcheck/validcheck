package io.github.validcheck;

import java.util.Collection;
import java.util.List;

/**
 * Base context for validation operations.
 *
 * <p>This class provides the underlying validation infrastructure used by both {@link
 * ConfiguredCheck} and {@link BatchValidationContext}. It contains all the check() method overloads
 * and manages the validation configuration.
 *
 * <p>This class is not intended for direct use by end users. Use {@link Check}, {@link
 * ConfiguredCheck}, or {@link BatchValidationContext} instead.
 *
 * @since 1.0
 */
public class ValidationContext {
  final ValidationConfig config;

  ValidationContext(ValidationConfig config) {
    this.config = config;
  }

  /**
   * Creates a validator for the given value without a parameter name.
   *
   * <p>Use this for validating values where the parameter name is not important for error messages.
   * This method is available on {@link ConfiguredCheck} and {@link BatchValidationContext}
   * instances.
   *
   * <p>Example:
   *
   * <pre>{@code
   * var configured = new ConfiguredCheck(config);
   * configured.check(someValue).notNull();
   *
   * var batch = batch();
   * batch.check(result).satisfies(r -> r.isValid(), "must be valid");
   * }</pre>
   *
   * @param <T> the type of the value
   * @param value the value to validate
   * @return a validator for the value
   * @since 1.0
   */
  public <T> ValueValidator<T> check(T value) {
    return check(null, value);
  }

  /**
   * Creates a string validator for the given string value without a parameter name.
   *
   * <p>Use this for validating string values with access to string-specific validation methods.
   * This method is available on {@link ConfiguredCheck} and {@link BatchValidationContext}
   * instances.
   *
   * <p>Example:
   *
   * <pre>{@code
   * var configured = new ConfiguredCheck(config);
   * configured.check(email).notNull().isEmail();
   *
   * var batch = batch();
   * batch.check(text).notEmpty().maxLength(100);
   * }</pre>
   *
   * @param value the string value to validate
   * @return a string validator for the value
   * @since 1.0
   */
  public StringValidator check(String value) {
    return new StringValidator(this, null, value);
  }

  /**
   * Creates a numeric validator for the given Integer value without a parameter name.
   *
   * <p>Use this for validating Integer objects with access to numeric-specific validation methods.
   * This method is available on {@link ConfiguredCheck} and {@link BatchValidationContext}
   * instances.
   *
   * <p>Example:
   *
   * <pre>{@code
   * var configured = new ConfiguredCheck(config);
   * configured.check(count).notNull().isPositive();
   *
   * var batch = batch();
   * batch.check(limit).when(limit != null, v -> v.max(1000));
   * }</pre>
   *
   * @param value the Integer value to validate (may be null)
   * @return a numeric validator for the value
   * @since 1.0
   */
  public NumericValidator<Integer> check(Integer value) {
    return new NumericValidator<>(this, null, value);
  }

  /**
   * Creates a numeric validator for the given int value without a parameter name.
   *
   * <p>Use this for validating integer values with access to numeric-specific validation methods.
   * This method is available on {@link ConfiguredCheck} and {@link BatchValidationContext}
   * instances.
   *
   * <p>Example:
   *
   * <pre>{@code
   * var configured = new ConfiguredCheck(config);
   * configured.check(age).isPositive().max(120);
   *
   * var batch = batch();
   * batch.check(quantity).isNonNegative().min(1);
   * }</pre>
   *
   * @param value the int value to validate
   * @return a numeric validator for the value
   * @since 1.0
   */
  public NumericValidator<Integer> check(int value) {
    return new NumericValidator<>(this, null, value);
  }

  /**
   * Creates a numeric validator for the given Long value without a parameter name.
   *
   * @param value the Long value to validate (may be null)
   * @return a numeric validator for the value
   * @since 1.0
   */
  public NumericValidator<Long> check(Long value) {
    return new NumericValidator<>(this, null, value);
  }

  /**
   * Creates a numeric validator for the given long value without a parameter name.
   *
   * @param value the long value to validate
   * @return a numeric validator for the value
   * @since 1.0
   */
  public NumericValidator<Long> check(long value) {
    return new NumericValidator<>(this, null, value);
  }

  /**
   * Creates a numeric validator for the given Double value without a parameter name.
   *
   * @param value the Double value to validate (may be null)
   * @return a numeric validator for the value
   * @since 1.0
   */
  public NumericValidator<Double> check(Double value) {
    return new NumericValidator<>(this, null, value);
  }

  /**
   * Creates a numeric validator for the given double value without a parameter name.
   *
   * @param value the double value to validate
   * @return a numeric validator for the value
   * @since 1.0
   */
  public NumericValidator<Double> check(double value) {
    return new NumericValidator<>(this, null, value);
  }

  /**
   * Creates a numeric validator for the given Float value without a parameter name.
   *
   * @param value the Float value to validate (may be null)
   * @return a numeric validator for the value
   * @since 1.0
   */
  public NumericValidator<Float> check(Float value) {
    return new NumericValidator<>(this, null, value);
  }

  /**
   * Creates a numeric validator for the given float value without a parameter name.
   *
   * @param value the float value to validate
   * @return a numeric validator for the value
   * @since 1.0
   */
  public NumericValidator<Float> check(float value) {
    return new NumericValidator<>(this, null, value);
  }

  /**
   * Creates a numeric validator for the given Number value without a parameter name.
   *
   * <p>Use this for validating any Number subtype (BigDecimal, BigInteger, etc.) with access to
   * numeric-specific validation methods. This method is available on {@link ConfiguredCheck} and
   * {@link BatchValidationContext} instances.
   *
   * <p>Example:
   *
   * <pre>{@code
   * var configured = new ConfiguredCheck(config);
   * configured.check(bigDecimal).notNull().isPositive();
   *
   * var batch = batch();
   * batch.check(bigInteger).min(BigInteger.ZERO);
   * }</pre>
   *
   * @param <T> the Number subtype
   * @param value the Number value to validate (may be null)
   * @return a numeric validator for the value
   * @since 1.0
   */
  public <T extends Number> NumericValidator<T> check(T value) {
    return new NumericValidator<>(this, null, value);
  }

  /**
   * Creates a collection validator for the given Collection value without a parameter name.
   *
   * <p>Use this for validating collections (List, Set, etc.) with access to collection-specific
   * validation methods. This method is available on {@link ConfiguredCheck} and {@link
   * BatchValidationContext} instances.
   *
   * <p>Example:
   *
   * <pre>{@code
   * var configured = new ConfiguredCheck(config);
   * configured.check(items).notNull().notEmpty();
   *
   * var batch = batch();
   * batch.check(tags).minSize(1).maxSize(10);
   * }</pre>
   *
   * @param <E> the element type of the Collection
   * @param <T> the Collection subtype
   * @param value the Collection value to validate (may be null)
   * @return a collection validator for the value
   * @since 1.0
   */
  public <E, T extends Collection<E>> CollectionValidator<T> check(T value) {
    return new CollectionValidator<>(this, null, value);
  }

  /**
   * Creates a validator for the given named value.
   *
   * <p>The parameter name will be included in error messages to provide better context. This method
   * is available on {@link ConfiguredCheck} and {@link BatchValidationContext} instances.
   *
   * <p>Example:
   *
   * <pre>{@code
   * var configured = new ConfiguredCheck(config);
   * configured.check("userId", userId).notNull();
   *
   * var batch = batch();
   * batch.check("config", config).satisfies(c -> c.isValid(), "must be valid");
   * }</pre>
   *
   * @param <T> the type of the value
   * @param name the parameter name for error messages
   * @param value the value to validate
   * @return a validator for the named value
   * @since 1.0
   */
  public <T> ValueValidator<T> check(String name, T value) {
    return new ValueValidator<>(this, name, value);
  }

  /**
   * Creates a string validator for the given named string value.
   *
   * <p>The parameter name will be included in error messages and you get access to string-specific
   * validation methods. This method is available on {@link ConfiguredCheck} and {@link
   * BatchValidationContext} instances.
   *
   * <p>Example:
   *
   * <pre>{@code
   * var configured = new ConfiguredCheck(config);
   * configured.check("email", email).notNull().isEmail();
   *
   * var batch = batch();
   * batch.check("name", name).notEmpty().lengthBetween(2, 50);
   * }</pre>
   *
   * @param name the parameter name for error messages
   * @param value the string value to validate
   * @return a string validator for the named value
   * @since 1.0
   */
  public StringValidator check(String name, String value) {
    return new StringValidator(this, name, value);
  }

  /**
   * Creates a numeric validator for the given named Integer value.
   *
   * <p>The parameter name will be included in error messages and you get access to numeric-specific
   * validation methods. This method is available on {@link ConfiguredCheck} and {@link
   * BatchValidationContext} instances.
   *
   * @param name the parameter name for error messages
   * @param value the Integer value to validate (may be null)
   * @return a numeric validator for the named value
   * @since 1.0
   */
  public NumericValidator<Integer> check(String name, Integer value) {
    return new NumericValidator<>(this, name, value);
  }

  /**
   * Creates a numeric validator for the given named int value.
   *
   * <p>The parameter name will be included in error messages and you get access to numeric-specific
   * validation methods. This method is available on {@link ConfiguredCheck} and {@link
   * BatchValidationContext} instances.
   *
   * <p>Example:
   *
   * <pre>{@code
   * var configured = new ConfiguredCheck(config);
   * configured.check("age", age).isPositive().max(120);
   *
   * var batch = batch();
   * batch.check("quantity", quantity).isNonNegative().min(1);
   * }</pre>
   *
   * @param name the parameter name for error messages
   * @param value the int value to validate
   * @return a numeric validator for the named value
   * @since 1.0
   */
  public NumericValidator<Integer> check(String name, int value) {
    return new NumericValidator<>(this, name, value);
  }

  /**
   * Creates a numeric validator for the given named Long value.
   *
   * @param name the parameter name for error messages
   * @param value the Long value to validate (may be null)
   * @return a numeric validator for the named value
   * @since 1.0
   */
  public NumericValidator<Long> check(String name, Long value) {
    return new NumericValidator<>(this, name, value);
  }

  /**
   * Creates a numeric validator for the given named long value.
   *
   * @param name the parameter name for error messages
   * @param value the long value to validate
   * @return a numeric validator for the named value
   * @since 1.0
   */
  public NumericValidator<Long> check(String name, long value) {
    return new NumericValidator<>(this, name, value);
  }

  /**
   * Creates a numeric validator for the given named Double value.
   *
   * @param name the parameter name for error messages
   * @param value the Double value to validate (may be null)
   * @return a numeric validator for the named value
   * @since 1.0
   */
  public NumericValidator<Double> check(String name, Double value) {
    return new NumericValidator<>(this, name, value);
  }

  /**
   * Creates a numeric validator for the given named double value.
   *
   * @param name the parameter name for error messages
   * @param value the double value to validate
   * @return a numeric validator for the named value
   * @since 1.0
   */
  public NumericValidator<Double> check(String name, double value) {
    return new NumericValidator<>(this, name, value);
  }

  /**
   * Creates a numeric validator for the given named Float value.
   *
   * @param name the parameter name for error messages
   * @param value the Float value to validate (may be null)
   * @return a numeric validator for the named value
   * @since 1.0
   */
  public NumericValidator<Float> check(String name, Float value) {
    return new NumericValidator<>(this, name, value);
  }

  /**
   * Creates a numeric validator for the given named float value.
   *
   * @param name the parameter name for error messages
   * @param value the float value to validate
   * @return a numeric validator for the named value
   * @since 1.0
   */
  public NumericValidator<Float> check(String name, float value) {
    return new NumericValidator<>(this, name, value);
  }

  /**
   * Creates a numeric validator for the given named Number value.
   *
   * <p>Use this for validating any Number subtype (BigDecimal, BigInteger, etc.) with the parameter
   * name included in error messages. This method is available on {@link ConfiguredCheck} and {@link
   * BatchValidationContext} instances.
   *
   * <p>Example:
   *
   * <pre>{@code
   * var configured = new ConfiguredCheck(config);
   * configured.check("price", bigDecimal).notNull().isPositive();
   *
   * var batch = batch();
   * batch.check("id", bigInteger).min(BigInteger.ONE);
   * }</pre>
   *
   * @param <T> the Number subtype
   * @param name the parameter name for error messages
   * @param value the Number value to validate (may be null)
   * @return a numeric validator for the named value
   * @since 1.0
   */
  public <T extends Number> NumericValidator<T> check(String name, T value) {
    return new NumericValidator<>(this, name, value);
  }

  /**
   * Creates a collection validator for the given named Collection value.
   *
   * <p>The parameter name will be included in error messages and you get access to
   * collection-specific validation methods. This method is available on {@link ConfiguredCheck} and
   * {@link BatchValidationContext} instances.
   *
   * <p>Example:
   *
   * <pre>{@code
   * var configured = new ConfiguredCheck(config);
   * configured.check("items", items).notNull().notEmpty();
   *
   * var batch = batch();
   * batch.check("tags", tags).minSize(1).maxSize(10);
   * }</pre>
   *
   * @param <E> the element type of the Collection
   * @param <T> the Collection subtype
   * @param name the parameter name for error messages
   * @param value the Collection value to validate (may be null)
   * @return a collection validator for the named value
   * @since 1.0
   */
  public <E, T extends Collection<E>> CollectionValidator<T> check(String name, T value) {
    return new CollectionValidator<>(this, name, value);
  }

  /**
   * Immediately throws a ValidationException with the given message.
   *
   * <p>This method is available on {@link ConfiguredCheck} instances and can be used for custom
   * validation failures.
   *
   * <p>Example:
   *
   * <pre>{@code
   * var configured = new ConfiguredCheck(config);
   * if (someComplexCondition) {
   *   configured.fail("Complex validation failed");
   * }
   * }</pre>
   *
   * @param message the error message
   * @throws ValidationException always, with the given message
   * @since 1.0
   */
  public void fail(String message) {
    throwException(List.of(message));
  }

  final void throwException(List<String> errors) {
    var message = formatMessage(errors);
    throw config.fillStackTrace
        ? new ValidationException(message, errors)
        : new ValidationException(message, errors) {
          @Override
          public synchronized Throwable fillInStackTrace() {
            return this;
          }
        };
  }

  String formatMessage(List<String> errors) {
    return String.join(";", errors);
  }
}
