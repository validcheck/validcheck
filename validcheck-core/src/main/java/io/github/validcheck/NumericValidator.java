package io.github.validcheck;

import java.util.function.Consumer;
import java.util.function.Predicate;

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
}
