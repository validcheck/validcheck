package io.github.validcheck;

import java.util.Objects;

/**
 * Configuration class that controls validation behavior and exception characteristics.
 *
 * <p>This class allows customization of validation exception behavior, including whether to include
 * stack traces (for performance), whether to show actual values in error messages (for security),
 * and the maximum length of values displayed in error messages.
 *
 * <p>Example usage:
 *
 * <pre>{@code
 * // Performance-optimized config
 * var fastConfig = new ValidationConfig(false, true, 256);
 *
 * // Security-focused config
 * var secureConfig = new ValidationConfig(true, false, 64);
 *
 * var configured = Check.withConfig(secureConfig);
 * configured.check(password, "password").notNull();
 * }</pre>
 *
 * @since 1.0
 */
public class ValidationConfig {
  /**
   * Default validation configuration with all features enabled.
   *
   * <p>Settings:
   *
   * <ul>
   *   <li>fillStackTrace = true (includes full stack traces)
   *   <li>includeActualValue = true (shows actual values in error messages)
   *   <li>actualValueMaxLength = 128 (limits value display to 128 characters)
   * </ul>
   *
   * @since 1.0
   */
  public static final ValidationConfig DEFAULT = new ValidationConfig(true, true, null);

  final boolean fillStackTrace;
  final boolean includeActualValue;
  final int actualValueMaxLength;

  /**
   * Creates a new validation configuration with the specified settings.
   *
   * @param fillStackTrace whether to include stack traces in ValidationException (true for better
   *     debugging, false for better performance)
   * @param includeActualValue whether to include actual values in error messages (true for better
   *     debugging, false for security when handling sensitive data)
   * @param actualValueMaxLength maximum length of actual values displayed in error messages, or
   *     null to use the default of 128 characters
   * @since 1.0
   */
  public ValidationConfig(
      boolean fillStackTrace, boolean includeActualValue, Integer actualValueMaxLength) {
    this.fillStackTrace = fillStackTrace;
    this.includeActualValue = includeActualValue;
    this.actualValueMaxLength = Objects.requireNonNullElse(actualValueMaxLength, 128);
  }
}
