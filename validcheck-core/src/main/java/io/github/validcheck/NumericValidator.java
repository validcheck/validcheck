package io.github.validcheck;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * A validator for numeric values with number-specific validation methods.
 *
 * <p>This validator extends {@link ValueValidator} to provide numeric-specific validation methods
 * like range checks, sign validation, and comparison operations.
 *
 * <p>Example usage:
 *
 * <pre>{@code
 * check("age", age).isPositive().max(120);
 * check("quantity", quantity).isNonNegative().min(1);
 * check("price", price).between(BigDecimal.ZERO, BigDecimal.valueOf(1000));
 * }</pre>
 *
 * @param <T> the number type (Integer, Long, Double, Float, BigDecimal, etc.)
 * @since 1.0
 */
public class NumericValidator<T extends Number> extends ValueValidator<T> {

  NumericValidator(ValidationContext context, String name, T value) {
    super(context, name, value);
  }

  @Override
  public NumericValidator<T> notNull() {
    return (NumericValidator<T>) super.notNull();
  }

  @Override
  public NumericValidator<T> isNull() {
    return (NumericValidator<T>) super.isNull();
  }

  @Override
  public NumericValidator<T> satisfies(Predicate<T> predicate, String message) {
    return (NumericValidator<T>) super.satisfies(predicate, message);
  }

  @Override
  public NumericValidator<T> when(boolean condition, Consumer<ValueValidator<T>> then) {
    return (NumericValidator<T>) super.when(condition, then);
  }

  @Override
  public NumericValidator<T> withMessage(String customMessage) {
    return (NumericValidator<T>) super.withMessage(customMessage);
  }

  /**
   * Conditionally applies validation logic if the condition is true.
   *
   * <p>This method provides access to numeric-specific validation methods in the conditional block,
   * unlike the generic {@link #when(boolean, Consumer)} method.
   *
   * <p>Example:
   *
   * <pre>{@code
   * check("discount", discount)
   *   .whenNumeric(isPercentage, validator -> validator.between(0.0, 100.0));
   * }</pre>
   *
   * @param condition the condition to check
   * @param then the validation logic to apply if condition is true
   * @return this validator for method chaining
   * @since 1.0
   */
  public NumericValidator<T> whenNumeric(boolean condition, Consumer<NumericValidator<T>> then) {
    if (condition) {
      then.accept(this);
    }
    return this;
  }

  // --- Numeric specific methods --- //

  /**
   * Validates that the number is positive (greater than zero).
   *
   * <p>Example:
   *
   * <pre>{@code
   * check("age", age).isPositive();
   * check("price", price).isPositive();
   * }</pre>
   *
   * @return this validator for method chaining
   * @throws ValidationException if the number is not positive
   * @since 1.0
   */
  public NumericValidator<T> isPositive() {
    return (NumericValidator<T>)
        satisfiesInternal(n -> n.doubleValue() > 0, "must be positive", true);
  }

  /**
   * Validates that the number is negative (less than zero).
   *
   * <p>Example:
   *
   * <pre>{@code
   * check("debt", debt).isNegative();
   * }</pre>
   *
   * @return this validator for method chaining
   * @throws ValidationException if the number is not negative
   * @since 1.0
   */
  public NumericValidator<T> isNegative() {
    return (NumericValidator<T>)
        satisfiesInternal(n -> n.doubleValue() < 0, "must be negative", true);
  }

  /**
   * Validates that the number is exactly zero.
   *
   * <p>Example:
   *
   * <pre>{@code
   * check("balance", balance).isZero();
   * }</pre>
   *
   * @return this validator for method chaining
   * @throws ValidationException if the number is not zero
   * @since 1.0
   */
  public NumericValidator<T> isZero() {
    return (NumericValidator<T>) satisfiesInternal(n -> n.doubleValue() == 0, "must be zero", true);
  }

  /**
   * Validates that the number is greater than or equal to the specified minimum.
   *
   * <p>Example:
   *
   * <pre>{@code
   * check("age", age).min(18);
   * check("price", price).min(BigDecimal.ZERO);
   * }</pre>
   *
   * @param minimum the minimum allowed value (inclusive)
   * @return this validator for method chaining
   * @throws ValidationException if the number is less than the minimum
   * @since 1.0
   */
  public NumericValidator<T> min(T minimum) {
    return (NumericValidator<T>)
        satisfiesInternal(
            n -> n.doubleValue() >= minimum.doubleValue(),
            String.format("must be at least %s", minimum),
            true);
  }

  /**
   * Validates that the number is less than or equal to the specified maximum.
   *
   * <p>Example:
   *
   * <pre>{@code
   * check("age", age).max(120);
   * check("percentage", percentage).max(100.0);
   * }</pre>
   *
   * @param maximum the maximum allowed value (inclusive)
   * @return this validator for method chaining
   * @throws ValidationException if the number is greater than the maximum
   * @since 1.0
   */
  public NumericValidator<T> max(T maximum) {
    return (NumericValidator<T>)
        satisfiesInternal(
            n -> n.doubleValue() <= maximum.doubleValue(),
            String.format("must be at most %s", maximum),
            true);
  }

  /**
   * Validates that the number is within the specified range (inclusive).
   *
   * <p>Example:
   *
   * <pre>{@code
   * check("grade", grade).between(0, 100);
   * check("temperature", temperature).between(-273.15, 1000.0);
   * }</pre>
   *
   * @param minimum the minimum allowed value (inclusive)
   * @param maximum the maximum allowed value (inclusive)
   * @return this validator for method chaining
   * @throws ValidationException if the number is outside the specified range
   * @since 1.0
   */
  public NumericValidator<T> between(T minimum, T maximum) {
    return (NumericValidator<T>)
        satisfiesInternal(
            n -> {
              double val = n.doubleValue();
              return val >= minimum.doubleValue() && val <= maximum.doubleValue();
            },
            String.format("must be between %s and %s", minimum, maximum),
            true);
  }

  /**
   * Validates that the number is non-negative (greater than or equal to zero).
   *
   * <p>Example:
   *
   * <pre>{@code
   * check("quantity", quantity).isNonNegative();
   * check("distance", distance).isNonNegative();
   * }</pre>
   *
   * @return this validator for method chaining
   * @throws ValidationException if the number is negative
   * @since 1.0
   */
  public NumericValidator<T> isNonNegative() {
    return (NumericValidator<T>)
        satisfiesInternal(n -> n.doubleValue() >= 0, "must be non-negative", true);
  }

  /**
   * Validates that the number is not zero (either positive or negative).
   *
   * <p>Example:
   *
   * <pre>{@code
   * check("divisor", divisor).isNonZero();
   * check("velocity", velocity).isNonZero();
   * }</pre>
   *
   * @return this validator for method chaining
   * @throws ValidationException if the number is zero
   * @since 1.0
   */
  public NumericValidator<T> isNonZero() {
    return (NumericValidator<T>)
        satisfiesInternal(n -> n.doubleValue() != 0, "must be non-zero", true);
  }
}
