package io.github.validcheck;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * A validator for String values with string-specific validation methods.
 *
 * <p>This validator extends {@link ValueValidator} to provide string-specific validation methods
 * like length checks, pattern matching, and format validation.
 *
 * <p>Example usage:
 *
 * <pre>{@code
 * check("email", email).notNull().isEmail();
 * check("name", name).notEmpty().lengthBetween(2, 50);
 * check("code", code).matches("[A-Z]{3}-\\d{4}");
 * }</pre>
 *
 * @since 1.0
 */
public class StringValidator extends ValueValidator<String> {

  private static final String EMAIL_REGEX =
      "^[\\p{L}\\p{N}._%+-]{1,64}@[\\p{L}\\p{N}.-]{1,253}\\.[\\p{L}]{2,63}$";
  private static final int MAX_EMAIL_LENGTH = 320; // RFC 5321 limit

  StringValidator(ValidationContext context, String name, String value) {
    super(context, name, value);
  }

  @Override
  public StringValidator notNull() {
    return (StringValidator) super.notNull();
  }

  @Override
  public StringValidator isNull() {
    return (StringValidator) super.isNull();
  }

  @Override
  public StringValidator satisfies(Predicate<String> predicate, String message) {
    return (StringValidator) super.satisfies(predicate, message);
  }

  @Override
  public StringValidator when(boolean condition, Consumer<ValueValidator<String>> then) {
    return (StringValidator) super.when(condition, then);
  }

  @Override
  public StringValidator withMessage(String customMessage) {
    return (StringValidator) super.withMessage(customMessage);
  }

  @Override
  public StringValidator oneOf(String... values) {
    return (StringValidator) super.oneOf(values);
  }

  /**
   * Conditionally applies validation logic if the condition is true.
   *
   * <p>This method provides access to string-specific validation methods in the conditional block,
   * unlike the generic {@link #when(boolean, Consumer)} method.
   *
   * <p>Example:
   *
   * <pre>{@code
   * check("url", url)
   *   .whenString(url.startsWith("https"), validator -> validator.minLength(8));
   * }</pre>
   *
   * @param condition the condition to check
   * @param then the validation logic to apply if condition is true
   * @return this validator for method chaining
   * @since 1.0
   */
  public StringValidator whenString(boolean condition, Consumer<StringValidator> then) {
    if (condition) {
      then.accept(this);
    }
    return this;
  }

  // --- String specific methods --- //

  /**
   * Validates that the string is empty ("").
   *
   * <p>Example:
   *
   * <pre>{@code
   * check("optional", optional).empty();
   * }</pre>
   *
   * @return this validator for method chaining
   * @throws ValidationException if the string is not empty
   * @since 1.0
   */
  public StringValidator empty() {
    return (StringValidator) satisfiesInternal(String::isEmpty, "must be empty", true);
  }

  /**
   * Validates that the string is not empty.
   *
   * <p>A string is considered empty if it has zero length. This does not check for null values -
   * use {@link #notNull()} first if needed.
   *
   * <p>Example:
   *
   * <pre>{@code
   * check("name", name).notNull().notEmpty();
   * }</pre>
   *
   * @return this validator for method chaining
   * @throws ValidationException if the string is empty
   * @since 1.0
   */
  public StringValidator notEmpty() {
    return (StringValidator) satisfiesInternal(s -> !s.isEmpty(), "must not be empty", false);
  }

  /**
   * Validates that the string is not null or empty.
   *
   * <p>This is a convenience method equivalent to calling {@link #notNull()} followed by {@link
   * #notEmpty()}. Use this when the string must have a value.
   *
   * <p>For strings that should have meaningful content (not just whitespace), consider using {@link
   * #hasText()} instead.
   *
   * <p>Example:
   *
   * <pre>{@code
   * check("username", username).notNullOrEmpty();
   * check("email", email).notNullOrEmpty().isEmail();
   * }</pre>
   *
   * @return this validator for method chaining
   * @throws ValidationException if the string is null or empty
   * @since 1.0
   */
  public StringValidator notNullOrEmpty() {
    return (StringValidator)
        satisfiesInternal(s -> s != null && !s.isEmpty(), "must not be null or empty", false);
  }

  /**
   * Validates that the string has text (not null and not empty after trimming).
   *
   * <p>This method checks that the string is not null and contains at least one non-whitespace
   * character.
   *
   * <p>Example:
   *
   * <pre>{@code
   * check("comment", comment).hasText();
   * }</pre>
   *
   * @return this validator for method chaining
   * @throws ValidationException if the string is null or contains only whitespace
   * @since 1.0
   */
  public StringValidator hasText() {
    return (StringValidator)
        satisfiesInternal(s -> s != null && !s.trim().isEmpty(), "must have text", false);
  }

  /**
   * Validates that the string has at least the specified minimum length.
   *
   * <p>Example:
   *
   * <pre>{@code
   * check("password", password).minLength(8);
   * }</pre>
   *
   * @param minimum the minimum required length (inclusive)
   * @return this validator for method chaining
   * @throws ValidationException if the string is shorter than the minimum length
   * @since 1.0
   */
  public StringValidator minLength(int minimum) {
    return (StringValidator)
        satisfiesInternal(
            s -> s.length() >= minimum,
            String.format("must be at least %d characters long", minimum),
            true);
  }

  /**
   * Validates that the string has at most the specified maximum length.
   *
   * <p>Example:
   *
   * <pre>{@code
   * check("username", username).maxLength(20);
   * }</pre>
   *
   * @param maximum the maximum allowed length (inclusive)
   * @return this validator for method chaining
   * @throws ValidationException if the string is longer than the maximum length
   * @since 1.0
   */
  public StringValidator maxLength(int maximum) {
    return (StringValidator)
        satisfiesInternal(
            s -> s.length() <= maximum,
            String.format("must be at most %d characters long", maximum),
            true);
  }

  /**
   * Validates that the string has exactly the specified length.
   *
   * <p>Example:
   *
   * <pre>{@code
   * check("countryCode", countryCode).length(2);
   * }</pre>
   *
   * @param exact the exact required length
   * @return this validator for method chaining
   * @throws ValidationException if the string length is not exactly as specified
   * @since 1.0
   */
  public StringValidator length(int exact) {
    return (StringValidator)
        satisfiesInternal(
            s -> s.length() == exact,
            String.format("must be exactly %d characters long", exact),
            true);
  }

  /**
   * Validates that the string matches the specified regular expression pattern.
   *
   * <p>Example:
   *
   * <pre>{@code
   * check("phone", phone).matches("\\d{10}");
   * check("code", code).matches("[A-Z]{3}-\\d{4}");
   * }</pre>
   *
   * @param regex the regular expression pattern to match
   * @return this validator for method chaining
   * @throws ValidationException if the string does not match the pattern
   * @since 1.0
   */
  public StringValidator matches(String regex) {
    return (StringValidator)
        satisfiesInternal(
            s -> s.matches(regex), String.format("must match pattern %s", regex), true);
  }

  /**
   * Validates that the string starts with the specified prefix.
   *
   * <p>Example:
   *
   * <pre>{@code
   * check("url", url).startsWith("https://");
   * }</pre>
   *
   * @param prefix the required prefix
   * @return this validator for method chaining
   * @throws ValidationException if the string does not start with the prefix
   * @since 1.0
   */
  public StringValidator startsWith(String prefix) {
    return (StringValidator)
        satisfiesInternal(
            s -> s.startsWith(prefix), String.format("must start with '%s'", prefix), true);
  }

  /**
   * Validates that the string ends with the specified suffix.
   *
   * <p>Example:
   *
   * <pre>{@code
   * check("filename", filename).endsWith(".txt");
   * }</pre>
   *
   * @param suffix the required suffix
   * @return this validator for method chaining
   * @throws ValidationException if the string does not end with the suffix
   * @since 1.0
   */
  public StringValidator endsWith(String suffix) {
    return (StringValidator)
        satisfiesInternal(
            s -> s.endsWith(suffix), String.format("must end with '%s'", suffix), true);
  }

  /**
   * Validates that the string length is within the specified range (inclusive).
   *
   * <p>Example:
   *
   * <pre>{@code
   * check("name", name).lengthBetween(2, 50);
   * }</pre>
   *
   * @param minimum the minimum required length (inclusive)
   * @param maximum the maximum allowed length (inclusive)
   * @return this validator for method chaining
   * @throws ValidationException if the string length is outside the specified range
   * @since 1.0
   */
  public StringValidator lengthBetween(int minimum, int maximum) {
    return (StringValidator)
        satisfiesInternal(
            s -> s.length() >= minimum && s.length() <= maximum,
            String.format("must be between %d and %d characters long", minimum, maximum),
            true);
  }

  /**
   * Validates that the string is a valid email address format.
   *
   * <p>This performs basic email validation that supports Unicode characters and follows a
   * simplified RFC 5322 pattern with ReDoS protection. It includes length limits (320 chars total,
   * 64 for local part, 253 for domain) and bounded quantifiers to prevent catastrophic
   * backtracking. For production use with complex requirements, consider using a more comprehensive
   * email validation library.
   *
   * <p>Example:
   *
   * <pre>{@code
   * check("email", email).notNull().isEmail();
   * }</pre>
   *
   * @return this validator for method chaining
   * @throws ValidationException if the string is not a valid email format
   * @since 1.0
   */
  public StringValidator isEmail() {
    return (StringValidator)
        satisfiesInternal(
            s -> s.length() <= MAX_EMAIL_LENGTH && s.matches(EMAIL_REGEX),
            "must be a valid email address",
            true);
  }

  /**
   * Validates that the string is blank (null or contains only whitespace).
   *
   * <p>A string is considered blank if it is null or contains only whitespace characters after
   * trimming.
   *
   * <p>Example:
   *
   * <pre>{@code
   * check("optional", optional).isBlank();
   * }</pre>
   *
   * @return this validator for method chaining
   * @throws ValidationException if the string is not blank
   * @since 1.0
   */
  public StringValidator isBlank() {
    return (StringValidator)
        satisfiesInternal(s -> s == null || s.trim().isEmpty(), "must be blank", true);
  }
}
