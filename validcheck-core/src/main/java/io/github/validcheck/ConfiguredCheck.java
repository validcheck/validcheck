package io.github.validcheck;

/**
 * A validation context that uses custom configuration settings.
 *
 * <p>This class provides the same validation methods as the static {@link Check} class but with
 * custom configuration for behavior like stack trace generation, value inclusion in error messages,
 * and value length limits.
 *
 * <p>Example usage:
 *
 * <pre>{@code
 * var config = new ValidationConfig(false, false, 256);
 * var configuredCheck = new ConfiguredCheck(config);
 * configuredCheck.check(password, "password").notNull().notEmpty();
 * }</pre>
 *
 * @since 1.0
 */
public class ConfiguredCheck extends ValidationContext {

  ConfiguredCheck(ValidationConfig config) {
    super(config);
  }

  /**
   * Asserts that a boolean condition is true, throwing a ValidationException if false.
   *
   * <p>This method uses the configured settings for exception behavior.
   *
   * <p>Example:
   *
   * <pre>{@code
   * var configured = new ConfiguredCheck(config);
   * configured.isTrue(user.isActive(), "User must be active");
   * }</pre>
   *
   * @param truth the condition to check
   * @param message the error message if the condition is false
   * @throws ValidationException if the condition is false
   * @since 1.0
   */
  public void isTrue(boolean truth, String message) {
    if (!truth) {
      fail(message);
    }
  }

  /**
   * Asserts that a boolean condition is false, throwing a ValidationException if true.
   *
   * <p>This method uses the configured settings for exception behavior.
   *
   * <p>Example:
   *
   * <pre>{@code
   * var configured = new ConfiguredCheck(config);
   * configured.isFalse(user.isBlocked(), "User must not be blocked");
   * }</pre>
   *
   * @param lie the condition to check (should be false)
   * @param message the error message if the condition is true
   * @throws ValidationException if the condition is true
   * @since 1.0
   */
  public void isFalse(boolean lie, String message) {
    isTrue(!lie, message);
  }

  /**
   * Creates a new batch validation context using the same configuration.
   *
   * <p>The returned batch context will use the same configuration settings as this configured check
   * instance.
   *
   * <p>Example:
   *
   * <pre>{@code
   * var configured = new ConfiguredCheck(config);
   * var batch = configured.batch();
   * batch.check(name, "name").notNull();
   * batch.check(age, "age").isPositive();
   * batch.validate();
   * }</pre>
   *
   * @return a new batch validation context with the same configuration
   * @since 1.0
   */
  public BatchValidationContext batch() {
    return new BatchValidationContext(config);
  }
}
