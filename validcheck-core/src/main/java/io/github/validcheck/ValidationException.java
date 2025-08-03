package io.github.validcheck;

import java.util.Collections;
import java.util.List;

/**
 * Exception thrown when validation fails.
 *
 * <p>This exception extends {@link IllegalArgumentException} and contains a list of all validation
 * errors that occurred. In fail-fast mode, it contains a single error. In batch mode, it can
 * contain multiple errors collected during validation.
 *
 * <p>Example usage:
 *
 * <pre>{@code
 * try {
 *   check("age", -5).isPositive();
 * } catch (ValidationException e) {
 *   System.out.println(e.getMessage()); // "'age' must be positive, but it was -5"
 *   List<String> errors = e.errors();   // ["'age' must be positive, but it was -5"]
 * }
 * }</pre>
 *
 * @since 1.0
 */
public class ValidationException extends IllegalArgumentException {
  /** The list of validation error messages. */
  private final List<String> errors;

  ValidationException(String message, List<String> errors) {
    super(message);
    this.errors = Collections.unmodifiableList(errors);
  }

  /**
   * Returns the list of validation error messages.
   *
   * <p>In fail-fast validation, this list contains a single error message. In batch validation,
   * this list contains all collected error messages.
   *
   * <p>Example:
   *
   * <pre>{@code
   * try {
   *   var validation = batch();
   *   validation.check("name", "").notEmpty();
   *   validation.check("age", -1).isPositive();
   *   validation.validate();
   * } catch (ValidationException e) {
   *   List<String> allErrors = e.errors();
   *   // ["'name' must not be empty", "'age' must be positive, but it was -1"]
   * }
   * }</pre>
   *
   * @return an unmodifiable list of error messages
   * @since 1.0
   */
  public List<String> errors() {
    return errors;
  }
}
