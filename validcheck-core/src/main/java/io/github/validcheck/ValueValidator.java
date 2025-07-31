package io.github.validcheck;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Base validator for any type of value with common validation methods.
 *
 * <p>This is the base class for all validators, providing common validation methods like null
 * checks and custom predicate validation. Specific validator types like {@link StringValidator} and
 * {@link NumericValidator} extend this class to provide type-specific validation methods.
 *
 * <p>Example usage:
 *
 * <pre>{@code
 * check("value", value).notNull();
 * check("config", config).satisfies(c -> c.isValid(), "must be valid");
 * check("optional", optional).when(optional != null, v -> v.satisfies(...));
 * }</pre>
 *
 * @param <T> the type of value being validated
 * @since 1.0
 */
public class ValueValidator<T> {
  private final ValidationContext context;
  private final String name;
  protected final T value;
  private String customMessage;

  ValueValidator(ValidationContext context, String name, T value) {
    this.context = context;
    this.name = name;
    this.value = value;
  }

  private String formatMessage(String message, boolean includeActualValue) {
    if (customMessage != null) {
      return customMessage;
    }

    var paramName = name == null ? "parameter" : String.format("'%s'", name);
    var error = String.format("%s " + message, paramName);
    if (includeActualValue && context.config.includeActualValue) {
      var stringValue = valueToString();
      var formattedValue =
          value instanceof String ? String.format("'%s'", stringValue) : stringValue;
      // limit the string length of string representation of the value
      return error + String.format(", but it was %s", formattedValue);
    }

    return error;
  }

  private String valueToString() {
    var string = String.valueOf(value);
    if (string.length() <= context.config.actualValueMaxLength) {
      return string;
    }

    return string.substring(0, context.config.actualValueMaxLength) + "...";
  }

  /**
   * Validates that the value is not null.
   *
   * <p>This is one of the most commonly used validation methods and should typically be the first
   * validation in a chain when null values are not allowed.
   *
   * <p>Example:
   *
   * <pre>{@code
   * check("name", name).notNull(); // other validations can follow
   * }</pre>
   *
   * @return this validator for method chaining
   * @throws ValidationException if the value is null
   * @since 1.0
   */
  public ValueValidator<T> notNull() {
    return satisfiesInternal(Objects::nonNull, "must not be null", false);
  }

  /**
   * Validates that the value is null.
   *
   * <p>This is useful for ensuring optional values are not provided when they shouldn't be.
   *
   * <p>Example:
   *
   * <pre>{@code
   * check("optionalField", optionalField).isNull();
   * }</pre>
   *
   * @return this validator for method chaining
   * @throws ValidationException if the value is not null
   * @since 1.0
   */
  public ValueValidator<T> isNull() {
    return satisfiesInternal(Objects::isNull, "must be null", true);
  }

  /**
   * Validates that the value satisfies the given predicate.
   *
   * <p>This is the most flexible validation method, allowing custom validation logic for any
   * condition not covered by other validation methods.
   *
   * <p>Example:
   *
   * <pre>{@code
   * check("user", user).satisfies(u -> u.isActive(), "must be active");
   * check("list", list).satisfies(l -> l.size() > 0, "must not be empty");
   * }</pre>
   *
   * @param predicate the condition that must be true for the value
   * @param message the error message if the predicate fails
   * @return this validator for method chaining
   * @throws ValidationException if the predicate returns false
   * @since 1.0
   */
  public ValueValidator<T> satisfies(Predicate<T> predicate, String message) {
    return satisfiesInternal(predicate, message, false);
  }

  final ValueValidator<T> satisfiesInternal(
      Predicate<T> predicate, String message, boolean includeActualValue) {
    if (!predicate.test(value)) {
      context.fail(formatMessage(message, includeActualValue));
    }

    return this;
  }

  /**
   * Conditionally applies validation logic if the condition is true.
   *
   * <p>This method allows conditional validation based on external conditions or the value itself.
   * The validation block is only executed if the condition is true.
   *
   * <p>Example:
   *
   * <pre>{@code
   * check("email", email)
   *   .when(email != null, validator -> validator.satisfies(e -> e.contains("@"), "must be valid email"));
   *
   * check("optional", optional)
   *   .when(isRequired, validator -> validator.notNull());
   * }</pre>
   *
   * @param condition the condition to check
   * @param then the validation logic to apply if condition is true
   * @return this validator for method chaining
   * @since 1.0
   */
  public ValueValidator<T> when(boolean condition, Consumer<ValueValidator<T>> then) {
    if (condition) {
      then.accept(this);
    }

    return this;
  }

  /**
   * Sets a custom error message for subsequent validation methods.
   *
   * <p>This method overrides the default error message for all validation methods called after it
   * in the chain. It does not affect error messages for validation methods called before it.
   *
   * <p>Example:
   *
   * <pre>{@code
   * check("email", email)
   *   .withMessage("Invalid customer email address").notNull().satisfies(e -> e.contains("@"));
   * }</pre>
   *
   * @param customMessage the custom error message to use for subsequent validations
   * @return this validator for method chaining
   * @since 1.0
   */
  public ValueValidator<T> withMessage(String customMessage) {
    this.customMessage = customMessage;
    return this;
  }

  /**
   * Validates that the value is one of the specified values.
   *
   * <p>This method checks that the value equals one of the provided values using {@link
   * Object#equals}. It's useful for validating enums, status codes, or any finite set of allowed
   * values.
   *
   * <p>Example:
   *
   * <pre>{@code
   * check("status", status).oneOf("ACTIVE", "INACTIVE", "PENDING");
   * check("priority", priority).oneOf(Priority.HIGH, Priority.MEDIUM, Priority.LOW);
   * }</pre>
   *
   * @param values the allowed values
   * @return this validator for method chaining
   * @throws ValidationException if the value is not one of the specified values
   * @since 1.0
   */
  @SafeVarargs
  public final ValueValidator<T> oneOf(T... values) {
    final var list = Arrays.asList(values);
    return satisfiesInternal(list::contains, String.format("must be one of %s", list), true);
  }
}
