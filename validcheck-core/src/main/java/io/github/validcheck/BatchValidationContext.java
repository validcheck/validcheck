package io.github.validcheck;

import java.util.ArrayList;
import java.util.List;
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
}
