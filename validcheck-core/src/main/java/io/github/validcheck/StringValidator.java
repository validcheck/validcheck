package io.github.validcheck;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class StringValidator extends ValueValidator<String> {

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

  // --- String specific methods --- //

  public StringValidator empty() {
    return (StringValidator) satisfies(String::isEmpty, "must be empty", true);
  }

  public StringValidator notEmpty() {
    return (StringValidator) satisfies(s -> !s.isEmpty(), "must not be empty", false);
  }

  public StringValidator hasText() {
    return (StringValidator)
        satisfies(s -> s != null && !s.trim().isEmpty(), "must have text", false);
  }

  public StringValidator minLength(int minimum) {
    return (StringValidator)
        satisfies(
            s -> s.length() >= minimum,
            String.format("must be at least %d characters long", minimum),
            true);
  }

  public StringValidator maxLength(int maximum) {
    return (StringValidator)
        satisfies(
            s -> s.length() <= maximum,
            String.format("must be at most %d characters long", maximum),
            true);
  }

  public StringValidator length(int exact) {
    return (StringValidator)
        satisfies(
            s -> s.length() == exact,
            String.format("must be exactly %d characters long", exact),
            true);
  }

  public StringValidator matches(String regex) {
    return (StringValidator)
        satisfies(s -> s.matches(regex), String.format("must match pattern %s", regex), false);
  }

  public StringValidator startsWith(String prefix) {
    return (StringValidator)
        satisfies(s -> s.startsWith(prefix), String.format("must start with '%s'", prefix), false);
  }

  public StringValidator endsWith(String suffix) {
    return (StringValidator)
        satisfies(s -> s.endsWith(suffix), String.format("must end with '%s'", suffix), false);
  }
}
