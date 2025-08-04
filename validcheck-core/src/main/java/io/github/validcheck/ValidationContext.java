package io.github.validcheck;

import java.util.Collection;
import java.util.List;
import java.util.Map;

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
    return check(value, null);
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
    return new StringValidator(this, value, null);
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
    return new NumericValidator<>(this, value, null);
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
    return new NumericValidator<>(this, value, null);
  }

  /**
   * Creates a numeric validator for the given Long value without a parameter name.
   *
   * @param value the Long value to validate (may be null)
   * @return a numeric validator for the value
   * @since 1.0
   */
  public NumericValidator<Long> check(Long value) {
    return new NumericValidator<>(this, value, null);
  }

  /**
   * Creates a numeric validator for the given long value without a parameter name.
   *
   * @param value the long value to validate
   * @return a numeric validator for the value
   * @since 1.0
   */
  public NumericValidator<Long> check(long value) {
    return new NumericValidator<>(this, value, null);
  }

  /**
   * Creates a numeric validator for the given Double value without a parameter name.
   *
   * @param value the Double value to validate (may be null)
   * @return a numeric validator for the value
   * @since 1.0
   */
  public NumericValidator<Double> check(Double value) {
    return new NumericValidator<>(this, value, null);
  }

  /**
   * Creates a numeric validator for the given double value without a parameter name.
   *
   * @param value the double value to validate
   * @return a numeric validator for the value
   * @since 1.0
   */
  public NumericValidator<Double> check(double value) {
    return new NumericValidator<>(this, value, null);
  }

  /**
   * Creates a numeric validator for the given Float value without a parameter name.
   *
   * @param value the Float value to validate (may be null)
   * @return a numeric validator for the value
   * @since 1.0
   */
  public NumericValidator<Float> check(Float value) {
    return new NumericValidator<>(this, value, null);
  }

  /**
   * Creates a numeric validator for the given float value without a parameter name.
   *
   * @param value the float value to validate
   * @return a numeric validator for the value
   * @since 1.0
   */
  public NumericValidator<Float> check(float value) {
    return new NumericValidator<>(this, value, null);
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
    return new NumericValidator<>(this, value, null);
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
    return new CollectionValidator<>(this, value, null);
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
   * configured.check(userId, "userId").notNull();
   *
   * var batch = batch();
   * batch.check(config, "config").satisfies(c -> c.isValid(), "must be valid");
   * }</pre>
   *
   * @param <T> the type of the value
   * @param value the value to validate
   * @param name the parameter name for error messages
   * @return a validator for the named value
   * @since 1.0
   */
  public <T> ValueValidator<T> check(T value, String name) {
    return new ValueValidator<>(this, value, name);
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
   * configured.check(email, "email").notNull().isEmail();
   *
   * var batch = batch();
   * batch.check(name, "name").notEmpty().lengthBetween(2, 50);
   * }</pre>
   *
   * @param value the string value to validate
   * @param name the parameter name for error messages
   * @return a string validator for the named value
   * @since 1.0
   */
  public StringValidator check(String value, String name) {
    return new StringValidator(this, value, name);
  }

  /**
   * Creates a numeric validator for the given named Integer value.
   *
   * <p>The parameter name will be included in error messages and you get access to numeric-specific
   * validation methods. This method is available on {@link ConfiguredCheck} and {@link
   * BatchValidationContext} instances.
   *
   * @param value the Integer value to validate (may be null)
   * @param name the parameter name for error messages
   * @return a numeric validator for the named value
   * @since 1.0
   */
  public NumericValidator<Integer> check(Integer value, String name) {
    return new NumericValidator<>(this, value, name);
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
   * configured.check(age, "age").isPositive().max(120);
   *
   * var batch = batch();
   * batch.check(quantity, "quantity").isNonNegative().min(1);
   * }</pre>
   *
   * @param value the int value to validate
   * @param name the parameter name for error messages
   * @return a numeric validator for the named value
   * @since 1.0
   */
  public NumericValidator<Integer> check(int value, String name) {
    return new NumericValidator<>(this, value, name);
  }

  /**
   * Creates a numeric validator for the given named Long value.
   *
   * @param value the Long value to validate (may be null)
   * @param name the parameter name for error messages
   * @return a numeric validator for the named value
   * @since 1.0
   */
  public NumericValidator<Long> check(Long value, String name) {
    return new NumericValidator<>(this, value, name);
  }

  /**
   * Creates a numeric validator for the given named long value.
   *
   * @param value the long value to validate
   * @param name the parameter name for error messages
   * @return a numeric validator for the named value
   * @since 1.0
   */
  public NumericValidator<Long> check(long value, String name) {
    return new NumericValidator<>(this, value, name);
  }

  /**
   * Creates a numeric validator for the given named Double value.
   *
   * @param value the Double value to validate (may be null)
   * @param name the parameter name for error messages
   * @return a numeric validator for the named value
   * @since 1.0
   */
  public NumericValidator<Double> check(Double value, String name) {
    return new NumericValidator<>(this, value, name);
  }

  /**
   * Creates a numeric validator for the given named double value.
   *
   * @param value the double value to validate
   * @param name the parameter name for error messages
   * @return a numeric validator for the named value
   * @since 1.0
   */
  public NumericValidator<Double> check(double value, String name) {
    return new NumericValidator<>(this, value, name);
  }

  /**
   * Creates a numeric validator for the given named Float value.
   *
   * @param value the Float value to validate (may be null)
   * @param name the parameter name for error messages
   * @return a numeric validator for the named value
   * @since 1.0
   */
  public NumericValidator<Float> check(Float value, String name) {
    return new NumericValidator<>(this, value, name);
  }

  /**
   * Creates a numeric validator for the given named float value.
   *
   * @param value the float value to validate
   * @param name the parameter name for error messages
   * @return a numeric validator for the named value
   * @since 1.0
   */
  public NumericValidator<Float> check(float value, String name) {
    return new NumericValidator<>(this, value, name);
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
   * configured.check(bigDecimal, "price").notNull().isPositive();
   *
   * var batch = batch();
   * batch.check(bigInteger, "id").min(BigInteger.ONE);
   * }</pre>
   *
   * @param <T> the Number subtype
   * @param value the Number value to validate (may be null)
   * @param name the parameter name for error messages
   * @return a numeric validator for the named value
   * @since 1.0
   */
  public <T extends Number> NumericValidator<T> check(T value, String name) {
    return new NumericValidator<>(this, value, name);
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
   * configured.check(items, "items").notNull().notEmpty();
   *
   * var batch = batch();
   * batch.check(tags, "tags").minSize(1).maxSize(10);
   * }</pre>
   *
   * @param <E> the element type of the Collection
   * @param <T> the Collection subtype
   * @param value the Collection value to validate (may be null)
   * @param name the parameter name for error messages
   * @return a collection validator for the named value
   * @since 1.0
   */
  public <E, T extends Collection<E>> CollectionValidator<T> check(T value, String name) {
    return new CollectionValidator<>(this, value, name);
  }

  /**
   * Creates a map validator for the given Map value without a parameter name.
   *
   * <p>Use this for validating maps (HashMap, TreeMap, etc.) with access to map-specific validation
   * methods.
   *
   * <p>Example:
   *
   * <pre>{@code
   * check(properties).notNull().notEmpty();
   * check(headers).minSize(1).maxSize(10);
   * }</pre>
   *
   * @param <K> the key type of the Map
   * @param <V> the value type of the Map
   * @param <T> the Map subtype
   * @param value the Map value to validate (may be null)
   * @return a map validator for the value
   * @since 1.0
   */
  public <K, V, T extends Map<K, V>> MapValidator<T> check(T value) {
    return new MapValidator<>(this, value, null);
  }

  /**
   * Creates a map validator for the given named Map value.
   *
   * <p>The parameter name will be included in error messages and you get access to map-specific
   * validation methods.
   *
   * <p>Example:
   *
   * <pre>{@code
   * check(config, "config").notNull().notEmpty();
   * check(headers, "headers").minSize(1).containsKey("Content-Type");
   * }</pre>
   *
   * @param <K> the key type of the Map
   * @param <V> the value type of the Map
   * @param <T> the Map subtype
   * @param value the Map value to validate (may be null)
   * @param name the parameter name for error messages
   * @return a map validator for the named value
   * @since 1.0
   */
  public <K, V, T extends Map<K, V>> MapValidator<T> check(T value, String name) {
    return new MapValidator<>(this, value, name);
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
