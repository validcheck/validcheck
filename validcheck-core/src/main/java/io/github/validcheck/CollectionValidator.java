package io.github.validcheck;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * A validator for Collection values with collection-specific validation methods.
 *
 * <p>This validator extends {@link ValueValidator} to provide collection-specific validation
 * methods like size checks and emptiness validation.
 *
 * <p>Example usage:
 *
 * <pre>{@code
 * check("items", items).notNull().notEmpty();
 * check("tags", tags).minSize(1).maxSize(10);
 * check("results", results).sizeBetween(5, 100);
 * }</pre>
 *
 * @param <T> the collection type (List, Set, etc.)
 * @since 1.0
 */
public class CollectionValidator<T extends Collection<?>> extends ValueValidator<T> {

  CollectionValidator(ValidationContext context, String name, T value) {
    super(context, name, value);
  }

  @Override
  public CollectionValidator<T> notNull() {
    return (CollectionValidator<T>) super.notNull();
  }

  @Override
  public CollectionValidator<T> isNull() {
    return (CollectionValidator<T>) super.isNull();
  }

  @Override
  public CollectionValidator<T> satisfies(Predicate<T> predicate, String message) {
    return (CollectionValidator<T>) super.satisfies(predicate, message);
  }

  @Override
  public CollectionValidator<T> when(boolean condition, Consumer<ValueValidator<T>> then) {
    return (CollectionValidator<T>) super.when(condition, then);
  }

  @Override
  public CollectionValidator<T> withMessage(String customMessage) {
    return (CollectionValidator<T>) super.withMessage(customMessage);
  }

  /**
   * Conditionally applies validation logic if the condition is true.
   *
   * <p>This method provides access to collection-specific validation methods in the conditional
   * block, unlike the generic {@link #when(boolean, Consumer)} method.
   *
   * <p>Example:
   *
   * <pre>{@code
   * check("items", items)
   *   .whenCollection(isRequired, validator -> validator.notEmpty());
   * }</pre>
   *
   * @param condition the condition to check
   * @param then the validation logic to apply if condition is true
   * @return this validator for method chaining
   * @since 1.0
   */
  public CollectionValidator<T> whenCollection(
      boolean condition, Consumer<CollectionValidator<T>> then) {
    if (condition) {
      then.accept(this);
    }
    return this;
  }

  // --- Collection specific methods --- //

  /**
   * Validates that the collection is empty (has no elements).
   *
   * <p>Example:
   *
   * <pre>{@code
   * check("errors", errors).empty();
   * }</pre>
   *
   * @return this validator for method chaining
   * @throws ValidationException if the collection is not empty
   * @since 1.0
   */
  public CollectionValidator<T> empty() {
    return (CollectionValidator<T>) satisfiesInternal(Collection::isEmpty, "must be empty", true);
  }

  /**
   * Validates that the collection is not empty (has at least one element).
   *
   * <p>This also ensures the collection is not null.
   *
   * <p>Example:
   *
   * <pre>{@code
   * check("items", items).notEmpty();
   * }</pre>
   *
   * @return this validator for method chaining
   * @throws ValidationException if the collection is null or empty
   * @since 1.0
   */
  public CollectionValidator<T> notEmpty() {
    return (CollectionValidator<T>)
        satisfiesInternal(c -> c != null && !c.isEmpty(), "must not be empty", false);
  }

  /**
   * Validates that the collection has exactly the specified size.
   *
   * <p>Example:
   *
   * <pre>{@code
   * check("coordinates", coordinates).size(2);
   * }</pre>
   *
   * @param expected the exact required size
   * @return this validator for method chaining
   * @throws ValidationException if the collection size is not exactly as expected
   * @since 1.0
   */
  public CollectionValidator<T> size(int expected) {
    return (CollectionValidator<T>)
        satisfiesInternal(
            c -> c.size() == expected, String.format("must have size %d", expected), true);
  }

  /**
   * Validates that the collection has at least the specified minimum number of elements.
   *
   * <p>Example:
   *
   * <pre>{@code
   * check("items", items).minSize(1);
   * }</pre>
   *
   * @param minimum the minimum required number of elements (inclusive)
   * @return this validator for method chaining
   * @throws ValidationException if the collection has fewer elements than the minimum
   * @since 1.0
   */
  public CollectionValidator<T> minSize(int minimum) {
    return (CollectionValidator<T>)
        satisfiesInternal(
            c -> c.size() >= minimum,
            String.format("must have at least %d elements", minimum),
            true);
  }

  /**
   * Validates that the collection has at most the specified maximum number of elements.
   *
   * <p>Example:
   *
   * <pre>{@code
   * check("tags", tags).maxSize(10);
   * }</pre>
   *
   * @param maximum the maximum allowed number of elements (inclusive)
   * @return this validator for method chaining
   * @throws ValidationException if the collection has more elements than the maximum
   * @since 1.0
   */
  public CollectionValidator<T> maxSize(int maximum) {
    return (CollectionValidator<T>)
        satisfiesInternal(
            c -> c.size() <= maximum,
            String.format("must have at most %d elements", maximum),
            true);
  }

  /**
   * Validates that the collection size is within the specified range (inclusive).
   *
   * <p>Example:
   *
   * <pre>{@code
   * check("results", results).sizeBetween(5, 100);
   * }</pre>
   *
   * @param minimum the minimum required number of elements (inclusive)
   * @param maximum the maximum allowed number of elements (inclusive)
   * @return this validator for method chaining
   * @throws ValidationException if the collection size is outside the specified range
   * @since 1.0
   */
  public CollectionValidator<T> sizeBetween(int minimum, int maximum) {
    return (CollectionValidator<T>)
        satisfiesInternal(
            c -> c.size() >= minimum && c.size() <= maximum,
            String.format("must have between %d and %d elements", minimum, maximum),
            true);
  }
}
