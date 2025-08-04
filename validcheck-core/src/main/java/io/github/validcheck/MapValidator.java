package io.github.validcheck;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * A validator for Map values with map-specific validation methods.
 *
 * <p>This validator extends {@link ValueValidator} to provide map-specific validation methods like
 * size checks, emptiness validation, and key/value presence checks.
 *
 * <p>Example usage:
 *
 * <pre>{@code
 * check(config, "config").notNull().notEmpty();
 * check(headers, "headers").minSize(1).maxSize(10);
 * check(properties, "properties").sizeBetween(5, 100);
 * check(metadata, "metadata").containsKey("version");
 * }</pre>
 *
 * @param <T> the map type (HashMap, TreeMap, etc.)
 * @since 1.0
 */
public class MapValidator<T extends Map<?, ?>> extends ValueValidator<T> {

  MapValidator(ValidationContext context, String name, T value) {
    super(context, name, value);
  }

  @Override
  public MapValidator<T> notNull() {
    return (MapValidator<T>) super.notNull();
  }

  @Override
  public MapValidator<T> isNull() {
    return (MapValidator<T>) super.isNull();
  }

  @Override
  public MapValidator<T> satisfies(Predicate<T> predicate, String message) {
    return (MapValidator<T>) super.satisfies(predicate, message);
  }

  @Override
  public MapValidator<T> when(boolean condition, Consumer<ValueValidator<T>> then) {
    return (MapValidator<T>) super.when(condition, then);
  }

  @Override
  public MapValidator<T> withMessage(String customMessage) {
    return (MapValidator<T>) super.withMessage(customMessage);
  }

  /**
   * Conditionally applies validation logic if the condition is true.
   *
   * <p>This method provides access to map-specific validation methods in the conditional block,
   * unlike the generic {@link #when(boolean, Consumer)} method.
   *
   * <p>Example:
   *
   * <pre>{@code
   * check(config, "config")
   *   .whenMap(isRequired, validator -> validator.notEmpty());
   * }</pre>
   *
   * @param condition the condition to check
   * @param then the validation logic to apply if condition is true
   * @return this validator for method chaining
   * @since 1.0
   */
  public MapValidator<T> whenMap(boolean condition, Consumer<MapValidator<T>> then) {
    if (condition) {
      then.accept(this);
    }
    return this;
  }

  // --- Map specific methods --- //

  /**
   * Validates that the map is empty (has no key-value pairs).
   *
   * <p>Example:
   *
   * <pre>{@code
   * check(errors, "errors").empty();
   * }</pre>
   *
   * @return this validator for method chaining
   * @throws ValidationException if the map is not empty
   * @since 1.0
   */
  public MapValidator<T> empty() {
    return (MapValidator<T>) satisfiesInternal(Map::isEmpty, "must be empty", true);
  }

  /**
   * Validates that the map is not empty (has at least one key-value pair).
   *
   * <p>This also ensures the map is not null.
   *
   * <p>Example:
   *
   * <pre>{@code
   * check(config, "config").notEmpty();
   * }</pre>
   *
   * @return this validator for method chaining
   * @throws ValidationException if the map is null or empty
   * @since 1.0
   */
  public MapValidator<T> notEmpty() {
    return (MapValidator<T>)
        satisfiesInternal(m -> m != null && !m.isEmpty(), "must not be empty", false);
  }

  /**
   * Validates that the map has exactly the specified size.
   *
   * <p>Example:
   *
   * <pre>{@code
   * check(coordinates, "coordinates").size(2);
   * }</pre>
   *
   * @param expected the exact required size
   * @return this validator for method chaining
   * @throws ValidationException if the map size is not exactly as expected
   * @since 1.0
   */
  public MapValidator<T> size(int expected) {
    return (MapValidator<T>)
        satisfiesInternal(
            m -> m.size() == expected, String.format("must have size %d", expected), true);
  }

  /**
   * Validates that the map has at least the specified minimum number of key-value pairs.
   *
   * <p>Example:
   *
   * <pre>{@code
   * check(config, "config").minSize(1);
   * }</pre>
   *
   * @param minimum the minimum required number of key-value pairs (inclusive)
   * @return this validator for method chaining
   * @throws ValidationException if the map has fewer key-value pairs than the minimum
   * @since 1.0
   */
  public MapValidator<T> minSize(int minimum) {
    return (MapValidator<T>)
        satisfiesInternal(
            m -> m.size() >= minimum,
            String.format("must have at least %d entry(ies)", minimum),
            true);
  }

  /**
   * Validates that the map has at most the specified maximum number of key-value pairs.
   *
   * <p>Example:
   *
   * <pre>{@code
   * check(headers, "headers").maxSize(10);
   * }</pre>
   *
   * @param maximum the maximum allowed number of key-value pairs (inclusive)
   * @return this validator for method chaining
   * @throws ValidationException if the map has more key-value pairs than the maximum
   * @since 1.0
   */
  public MapValidator<T> maxSize(int maximum) {
    return (MapValidator<T>)
        satisfiesInternal(
            m -> m.size() <= maximum,
            String.format("must have at most %d entry(ies)", maximum),
            true);
  }

  /**
   * Validates that the map size is within the specified range (inclusive).
   *
   * <p>Example:
   *
   * <pre>{@code
   * check(properties, "properties").sizeBetween(5, 100);
   * }</pre>
   *
   * @param minimum the minimum required number of key-value pairs (inclusive)
   * @param maximum the maximum allowed number of key-value pairs (inclusive)
   * @return this validator for method chaining
   * @throws ValidationException if the map size is outside the specified range
   * @since 1.0
   */
  public MapValidator<T> sizeBetween(int minimum, int maximum) {
    return (MapValidator<T>)
        satisfiesInternal(
            m -> m.size() >= minimum && m.size() <= maximum,
            String.format("must have between %d and %d entry(ies)", minimum, maximum),
            true);
  }

  /**
   * Validates that the map contains the specified key.
   *
   * <p>Example:
   *
   * <pre>{@code
   * check(config, "config").containsKey("version");
   * check(headers, "headers").containsKey("Content-Type");
   * }</pre>
   *
   * @param key the key that must be present in the map
   * @return this validator for method chaining
   * @throws ValidationException if the map does not contain the specified key
   * @since 1.0
   */
  public MapValidator<T> containsKey(Object key) {
    return (MapValidator<T>)
        satisfiesInternal(
            m -> m.containsKey(key), String.format("must contain key '%s'", key), false);
  }

  /**
   * Validates that the map does not contain the specified key.
   *
   * <p>Example:
   *
   * <pre>{@code
   * check(config, "config").doesNotContainKey("password");
   * }</pre>
   *
   * @param key the key that must not be present in the map
   * @return this validator for method chaining
   * @throws ValidationException if the map contains the specified key
   * @since 1.0
   */
  public MapValidator<T> doesNotContainKey(Object key) {
    return (MapValidator<T>)
        satisfiesInternal(
            m -> !m.containsKey(key), String.format("must not contain key '%s'", key), false);
  }

  /**
   * Validates that the map contains the specified value.
   *
   * <p>Example:
   *
   * <pre>{@code
   * check(statuses, "statuses").containsValue("ACTIVE");
   * }</pre>
   *
   * @param value the value that must be present in the map
   * @return this validator for method chaining
   * @throws ValidationException if the map does not contain the specified value
   * @since 1.0
   */
  public MapValidator<T> containsValue(Object value) {
    return (MapValidator<T>)
        satisfiesInternal(
            m -> m.containsValue(value), String.format("must contain value '%s'", value), false);
  }

  /**
   * Validates that the map does not contain the specified value.
   *
   * <p>Example:
   *
   * <pre>{@code
   * check(statuses, "statuses").doesNotContainValue("DELETED");
   * }</pre>
   *
   * @param value the value that must not be present in the map
   * @return this validator for method chaining
   * @throws ValidationException if the map contains the specified value
   * @since 1.0
   */
  public MapValidator<T> doesNotContainValue(Object value) {
    return (MapValidator<T>)
        satisfiesInternal(
            m -> !m.containsValue(value),
            String.format("must not contain value '%s'", value),
            false);
  }

  /**
   * Validates that the map contains all the specified keys.
   *
   * <p>Example:
   *
   * <pre>{@code
   * check(config, "config").containsAllKeys("host", "port", "database");
   * }</pre>
   *
   * @param keys the keys that must all be present in the map
   * @return this validator for method chaining
   * @throws ValidationException if the map does not contain all the specified keys
   * @since 1.0
   */
  public MapValidator<T> containsAllKeys(Object... keys) {
    return (MapValidator<T>)
        satisfiesInternal(
            m -> Arrays.stream(keys).allMatch(m::containsKey),
            String.format("must contain all keys %s", java.util.Arrays.toString(keys)),
            false);
  }
}
