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
    return (StringValidator) satisfies(String::isEmpty, "must not be empty", false);
  }
}
