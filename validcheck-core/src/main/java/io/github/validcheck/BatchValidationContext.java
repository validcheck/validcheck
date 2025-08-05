package io.github.validcheck;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * A validation context that collects multiple validation errors before throwing.
 *
 * <p>Unlike the standard {@link ValidationContext} which throws immediately on the first error,
 * this context accumulates all validation errors and throws them together when {@link #validate()}
 * is called.
 *
 * @since 1.0
 */
public class BatchValidationContext extends ValidationContext {
  private final List<String> errors;

  BatchValidationContext(ValidationConfig config) {
    super(config);
    errors = new ArrayList<>();
  }

  @Override
  public void fail(String message) {
    errors.add(message);
  }

  /**
   * Validates all collected errors and throws a ValidationException if any errors were recorded.
   *
   * <p>Call this method after performing all validations to throw a single exception containing all
   * validation errors.
   *
   * <p>Example:
   *
   * <pre>{@code
   * var validation = batch();
   * validation.check(name, "name").notNull().notEmpty();
   * validation.check(age, "age").isPositive().max(120);
   * validation.validate(); // Throws if any validation failed
   * }</pre>
   *
   * @throws ValidationException if any validation errors were recorded
   * @since 1.0
   */
  public void validate() {
    if (!errors.isEmpty()) {
      throwException(errors);
    }
  }

  /**
   * Includes all errors from another batch validation context into this one.
   *
   * <p>This is useful for combining validation results from different validation contexts.
   *
   * <p>Example:
   *
   * <pre>{@code
   * var mainValidation = batch();
   * var nestedValidation = batch();
   * nestedValidation.check(value, "field").notNull();
   * mainValidation.include(nestedValidation);
   * mainValidation.validate(); // Will include errors from both contexts
   * }</pre>
   *
   * @param context the batch validation context whose errors to include
   * @throws NullPointerException if context is null
   * @since 1.0
   */
  public void include(BatchValidationContext context) {
    errors.addAll(context.errors);
  }

  @Override
  String formatMessage(List<String> errors) {
    final var prefix = String.format("Validation failed with %d error(s):%n", errors.size());
    return errors.stream()
        .map(m -> "- " + m)
        .collect(Collectors.joining(System.lineSeparator(), prefix, ""));
  }

  /**
   * Returns true if any validation errors have been recorded.
   *
   * <p>Use this to check if validation has failed before calling {@link #validate()}.
   *
   * <p>Example:
   *
   * <pre>{@code
   * var validation = batch();
   * validation.check(name, "name").notNull();
   * if (validation.hasErrors()) {
   *   // Handle errors without throwing
   *   return;
   * }
   * // Continue with business logic
   * }</pre>
   *
   * @return true if there are validation errors, false otherwise
   * @since 1.0
   */
  public boolean hasErrors() {
    return !errors.isEmpty();
  }

  /**
   * Records a validation error if the given condition is false.
   *
   * <p>Unlike the static {@link Check#isTrue(boolean, String)} method, this does not immediately
   * throw an exception but adds the error to the batch for later validation.
   *
   * <p>Example:
   *
   * <pre>{@code
   * var validation = batch();
   * validation.isTrue(user.isActive(), "User must be active");
   * validation.isTrue(order.getItems().size() > 0, "Order must have items");
   * validation.validate(); // Throws with all errors if any condition was false
   * }</pre>
   *
   * @param truth the condition to check
   * @param message the error message if the condition is false
   * @since 1.0
   */
  public void isTrue(boolean truth, String message) {
    if (!truth) {
      fail(message);
    }
  }

  /**
   * Records a validation error if the given condition is true.
   *
   * <p>Unlike the static {@link Check#isFalse(boolean, String)} method, this does not immediately
   * throw an exception but adds the error to the batch for later validation.
   *
   * <p>Example:
   *
   * <pre>{@code
   * var validation = batch();
   * validation.isFalse(user.isBlocked(), "User must not be blocked");
   * validation.isFalse(account.isExpired(), "Account must not be expired");
   * validation.validate(); // Throws with all errors if any condition was true
   * }</pre>
   *
   * @param lie the condition to check (should be false)
   * @param message the error message if the condition is true
   * @since 1.0
   */
  public void isFalse(boolean lie, String message) {
    isTrue(!lie, message);
  }

  /**
   * Creates a validator for the given value and applies the provided validation logic.
   *
   * <p>This method allows for fluent batch validation by accepting a Consumer that defines the
   * validation rules to apply. The validation errors are collected rather than thrown immediately.
   *
   * <p>Example:
   *
   * <pre>{@code
   * var batch = batch();
   * batch.check(user, v -> v.notNull().satisfies(u -> u.isActive(), "must be active"));
   * batch.validate(); // Throws with all errors if validation failed
   * }</pre>
   *
   * @param <T> the type of the value
   * @param <V> the validator type
   * @param value the value to validate
   * @param check the validation logic to apply
   * @return this batch validation context for method chaining
   * @since 1.0
   */
  @SuppressWarnings("unchecked")
  public <T, V extends ValueValidator<T>> BatchValidationContext check(T value, Consumer<V> check) {
    check.accept((V) super.check(value));
    return this;
  }

  /**
   * Creates a validator for the given named value and applies the provided validation logic.
   *
   * <p>The parameter name will be included in error messages to provide better context. This method
   * allows for fluent batch validation by accepting a Consumer that defines the validation rules to
   * apply.
   *
   * <p>Example:
   *
   * <pre>{@code
   * var batch = batch();
   * batch.check(user, "user", v -> v.notNull().satisfies(u -> u.isActive(), "must be active"));
   * batch.validate(); // Throws with all errors if validation failed
   * }</pre>
   *
   * @param <T> the type of the value
   * @param <V> the validator type
   * @param value the value to validate
   * @param name the parameter name for error messages
   * @param check the validation logic to apply
   * @return this batch validation context for method chaining
   * @since 1.0
   */
  @SuppressWarnings("unchecked")
  public <T, V extends ValueValidator<T>> BatchValidationContext check(
      T value, String name, Consumer<V> check) {
    check.accept((V) super.check(value, name));
    return this;
  }

  /**
   * Creates a string validator for the given string value and applies the provided validation
   * logic.
   *
   * <p>Use this for validating string values with access to string-specific validation methods. The
   * validation errors are collected rather than thrown immediately.
   *
   * <p>Example:
   *
   * <pre>{@code
   * var batch = batch();
   * batch.check(email, v -> v.notNull().isEmail());
   * batch.check(text, v -> v.notEmpty().maxLength(100));
   * batch.validate(); // Throws with all errors if validation failed
   * }</pre>
   *
   * @param value the string value to validate
   * @param check the validation logic to apply
   * @return this batch validation context for method chaining
   * @since 1.0
   */
  public BatchValidationContext check(String value, Consumer<StringValidator> check) {
    check.accept(super.check(value));
    return this;
  }

  /**
   * Creates a string validator for the given named string value and applies the provided validation
   * logic.
   *
   * <p>The parameter name will be included in error messages and you get access to string-specific
   * validation methods. The validation errors are collected rather than thrown immediately.
   *
   * <p>Example:
   *
   * <pre>{@code
   * var batch = batch();
   * batch.check(email, "email", v -> v.notNull().isEmail());
   * batch.check(name, "name", v -> v.notEmpty().lengthBetween(2, 50));
   * batch.validate(); // Throws with all errors if validation failed
   * }</pre>
   *
   * @param value the string value to validate
   * @param name the parameter name for error messages
   * @param check the validation logic to apply
   * @return this batch validation context for method chaining
   * @since 1.0
   */
  public BatchValidationContext check(String value, String name, Consumer<StringValidator> check) {
    check.accept(super.check(value, name));
    return this;
  }

  /**
   * Creates a numeric validator for the given Number value and applies the provided validation
   * logic.
   *
   * <p>Use this for validating any Number subtype (BigDecimal, BigInteger, etc.) with access to
   * numeric-specific validation methods. The validation errors are collected rather than thrown
   * immediately.
   *
   * <p>Example:
   *
   * <pre>{@code
   * var batch = batch();
   * batch.check(age, v -> v.isPositive().max(120));
   * batch.check(count, v -> v.isNonNegative().min(1));
   * batch.validate(); // Throws with all errors if validation failed
   * }</pre>
   *
   * @param <T> the Number subtype
   * @param value the Number value to validate
   * @param check the validation logic to apply
   * @return this batch validation context for method chaining
   * @since 1.0
   */
  public <T extends Number> BatchValidationContext check(
      T value, Consumer<NumericValidator<T>> check) {
    check.accept(super.check(value));
    return this;
  }

  /**
   * Creates a numeric validator for the given named Number value and applies the provided
   * validation logic.
   *
   * <p>The parameter name will be included in error messages and you get access to numeric-specific
   * validation methods. Use this for validating any Number subtype (BigDecimal, BigInteger, etc.).
   * The validation errors are collected rather than thrown immediately.
   *
   * <p>Example:
   *
   * <pre>{@code
   * var batch = batch();
   * batch.check(age, "age", v -> v.isPositive().max(120));
   * batch.check(quantity, "quantity", v -> v.isNonNegative().min(1));
   * batch.validate(); // Throws with all errors if validation failed
   * }</pre>
   *
   * @param <T> the Number subtype
   * @param value the Number value to validate
   * @param name the parameter name for error messages
   * @param check the validation logic to apply
   * @return this batch validation context for method chaining
   * @since 1.0
   */
  public <T extends Number> BatchValidationContext check(
      T value, String name, Consumer<NumericValidator<T>> check) {
    check.accept(super.check(value, name));
    return this;
  }

  /**
   * Creates a collection validator for the given Collection value and applies the provided
   * validation logic.
   *
   * <p>Use this for validating collections (List, Set, etc.) with access to collection-specific
   * validation methods. The validation errors are collected rather than thrown immediately.
   *
   * <p>Example:
   *
   * <pre>{@code
   * var batch = batch();
   * batch.check(items, v -> v.notNull().notEmpty());
   * batch.check(tags, v -> v.minSize(1).maxSize(10));
   * batch.validate(); // Throws with all errors if validation failed
   * }</pre>
   *
   * @param <E> the element type of the Collection
   * @param <T> the Collection subtype
   * @param value the Collection value to validate
   * @param check the validation logic to apply
   * @return this batch validation context for method chaining
   * @since 1.0
   */
  @SuppressWarnings("unchecked")
  public <E, T extends Collection<E>> BatchValidationContext check(
      T value, Consumer<CollectionValidator<T>> check) {
    check.accept((CollectionValidator<T>) super.check(value));
    return this;
  }

  /**
   * Creates a collection validator for the given named Collection value and applies the provided
   * validation logic.
   *
   * <p>The parameter name will be included in error messages and you get access to
   * collection-specific validation methods. Use this for validating collections (List, Set, etc.).
   * The validation errors are collected rather than thrown immediately.
   *
   * <p>Example:
   *
   * <pre>{@code
   * var batch = batch();
   * batch.check(items, "items", v -> v.notNull().notEmpty());
   * batch.check(tags, "tags", v -> v.minSize(1).maxSize(10));
   * batch.validate(); // Throws with all errors if validation failed
   * }</pre>
   *
   * @param <E> the element type of the Collection
   * @param <T> the Collection subtype
   * @param value the Collection value to validate
   * @param name the parameter name for error messages
   * @param check the validation logic to apply
   * @return this batch validation context for method chaining
   * @since 1.0
   */
  @SuppressWarnings("unchecked")
  public <E, T extends Collection<E>> BatchValidationContext check(
      T value, String name, Consumer<CollectionValidator<T>> check) {
    check.accept((CollectionValidator<T>) super.check(value, name));
    return this;
  }

  /**
   * Creates a map validator for the given Map value and applies the provided validation logic.
   *
   * <p>Use this for validating maps (HashMap, TreeMap, etc.) with access to map-specific validation
   * methods. The validation errors are collected rather than thrown immediately.
   *
   * <p>Example:
   *
   * <pre>{@code
   * var batch = batch();
   * batch.check(properties, v -> v.notNull().notEmpty());
   * batch.check(headers, v -> v.minSize(1).maxSize(10));
   * batch.validate(); // Throws with all errors if validation failed
   * }</pre>
   *
   * @param <K> the key type of the Map
   * @param <V> the value type of the Map
   * @param <T> the Map subtype
   * @param value the Map value to validate
   * @param check the validation logic to apply
   * @return this batch validation context for method chaining
   * @since 1.0
   */
  @SuppressWarnings("unchecked")
  public <K, V, T extends Map<K, V>> BatchValidationContext check(
      T value, Consumer<MapValidator<T>> check) {
    check.accept((MapValidator<T>) super.check(value));
    return this;
  }

  /**
   * Creates a map validator for the given named Map value and applies the provided validation
   * logic.
   *
   * <p>The parameter name will be included in error messages and you get access to map-specific
   * validation methods. Use this for validating maps (HashMap, TreeMap, etc.). The validation
   * errors are collected rather than thrown immediately.
   *
   * <p>Example:
   *
   * <pre>{@code
   * var batch = batch();
   * batch.check(config, "config", v -> v.notNull().notEmpty());
   * batch.check(headers, "headers", v -> v.minSize(1).containsKey("Content-Type"));
   * batch.validate(); // Throws with all errors if validation failed
   * }</pre>
   *
   * @param <K> the key type of the Map
   * @param <V> the value type of the Map
   * @param <T> the Map subtype
   * @param value the Map value to validate
   * @param name the parameter name for error messages
   * @param check the validation logic to apply
   * @return this batch validation context for method chaining
   * @since 1.0
   */
  @SuppressWarnings("unchecked")
  public <K, V, T extends Map<K, V>> BatchValidationContext check(
      T value, String name, Consumer<MapValidator<T>> check) {
    check.accept((MapValidator<T>) super.check(value, name));
    return this;
  }
}
