package io.github.validcheck;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ValueValidator<T> {
  private final ValidationContext context;
  private final String name;
  protected final T value;

  ValueValidator(ValidationContext context, String name, T value) {
    this.context = context;
    this.name = name;
    this.value = value;
  }

  private final String formatMessage(String message, boolean includeActualValue) {
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

  public ValueValidator<T> notNull() {
    return satisfiesInternal(Objects::nonNull, "must not be null", false);
  }

  public ValueValidator<T> isNull() {
    return satisfiesInternal(Objects::isNull, "must be null", true);
  }

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

  public ValueValidator<T> when(boolean condition, Consumer<ValueValidator<T>> then) {
    if (condition) {
      then.accept(this);
    }

    return this;
  }
}
