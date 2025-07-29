package io.github.validcheck;

import java.util.Collection;
import java.util.List;

public class ValidationContext {
  final ValidationConfig config;

  ValidationContext(ValidationConfig config) {
    this.config = config;
  }

  public <T> ValueValidator<T> check(T value) {
    return check(null, value);
  }

  public <T> ValueValidator<T> check(String name, T value) {
    return new ValueValidator<>(this, name, value);
  }

  public StringValidator check(String name, String value) {
    return new StringValidator(this, name, value);
  }

  public NumericValidator<Integer> check(String name, Integer value) {
    return new NumericValidator<>(this, name, value);
  }

  public NumericValidator<Integer> check(String name, int value) {
    return new NumericValidator<>(this, name, value);
  }

  public NumericValidator<Long> check(String name, Long value) {
    return new NumericValidator<>(this, name, value);
  }

  public NumericValidator<Long> check(String name, long value) {
    return new NumericValidator<>(this, name, value);
  }

  public NumericValidator<Double> check(String name, Double value) {
    return new NumericValidator<>(this, name, value);
  }

  public NumericValidator<Double> check(String name, double value) {
    return new NumericValidator<>(this, name, value);
  }

  public NumericValidator<Float> check(String name, Float value) {
    return new NumericValidator<>(this, name, value);
  }

  public NumericValidator<Float> check(String name, float value) {
    return new NumericValidator<>(this, name, value);
  }

  public <T extends Number> NumericValidator<T> check(String name, T value) {
    return new NumericValidator<>(this, name, value);
  }

  public <T extends Collection<?>> CollectionValidator<T> check(String name, T value) {
    return new CollectionValidator<>(this, name, value);
  }

  public void fail(String message) {
    throwException(List.of(message));
  }

  final void throwException(List<String> errors) {
    var message = formatMessage(errors);
    throw config.fillStackTrace
        ? new ValidationException(message, errors)
        : new ValidationException(message, errors) {
          @Override
          public synchronized Throwable fillInStackTrace() {
            return this;
          }
        };
  }

  String formatMessage(List<String> errors) {
    return String.join(";", errors);
  }
}
