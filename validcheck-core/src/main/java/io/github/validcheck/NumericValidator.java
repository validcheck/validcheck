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

  // --- Numeric specific methods --- //

  public NumericValidator<T> isPositive() {
    return (NumericValidator<T>) satisfies(n -> n.doubleValue() > 0, "must be positive", true);
  }

  public NumericValidator<T> isNegative() {
    return (NumericValidator<T>) satisfies(n -> n.doubleValue() < 0, "must be negative", true);
  }

  public NumericValidator<T> isZero() {
    return (NumericValidator<T>) satisfies(n -> n.doubleValue() == 0, "must be zero", true);
  }

  public NumericValidator<T> min(T minimum) {
    return (NumericValidator<T>)
        satisfies(
            n -> n.doubleValue() >= minimum.doubleValue(),
            String.format("must be at least %s", minimum),
            true);
  }

  public NumericValidator<T> max(T maximum) {
    return (NumericValidator<T>)
        satisfies(
            n -> n.doubleValue() <= maximum.doubleValue(),
            String.format("must be at most %s", maximum),
            true);
  }

  public NumericValidator<T> between(T minimum, T maximum) {
    return (NumericValidator<T>)
        satisfies(
            n -> {
              double val = n.doubleValue();
              return val >= minimum.doubleValue() && val <= maximum.doubleValue();
            },
            String.format("must be between %s and %s", minimum, maximum),
            true);
  }
}
