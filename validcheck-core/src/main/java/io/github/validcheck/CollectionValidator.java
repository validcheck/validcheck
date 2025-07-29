package io.github.validcheck;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class CollectionValidator<T extends Collection<?>> extends ValueValidator<T> {

  CollectionValidator(ValidationContext context, String name, T value) {
    super(context, name, value);
  }

  @Override
  public CollectionValidator<T> notNull() {
    return (CollectionValidator<T>) super.notNull();
  }

  @Override
  public CollectionValidator<T> isNull() {
    return (CollectionValidator<T>) super.isNull();
  }

  @Override
  public CollectionValidator<T> satisfies(Predicate<T> predicate, String message) {
    return (CollectionValidator<T>) super.satisfies(predicate, message);
  }

  @Override
  public CollectionValidator<T> when(boolean condition, Consumer<ValueValidator<T>> then) {
    return (CollectionValidator<T>) super.when(condition, then);
  }

  // --- Collection specific methods --- //

  public CollectionValidator<T> empty() {
    return (CollectionValidator<T>) satisfiesInternal(Collection::isEmpty, "must be empty", true);
  }

  public CollectionValidator<T> notEmpty() {
    return (CollectionValidator<T>)
        satisfiesInternal(c -> c != null && !c.isEmpty(), "must not be empty", false);
  }

  public CollectionValidator<T> size(int expected) {
    return (CollectionValidator<T>)
        satisfiesInternal(
            c -> c.size() == expected, String.format("must have size %d", expected), true);
  }

  public CollectionValidator<T> minSize(int minimum) {
    return (CollectionValidator<T>)
        satisfiesInternal(
            c -> c.size() >= minimum,
            String.format("must have at least %d elements", minimum),
            true);
  }

  public CollectionValidator<T> maxSize(int maximum) {
    return (CollectionValidator<T>)
        satisfiesInternal(
            c -> c.size() <= maximum,
            String.format("must have at most %d elements", maximum),
            true);
  }

  public CollectionValidator<T> sizeBetween(int minimum, int maximum) {
    return (CollectionValidator<T>)
        satisfiesInternal(
            c -> c.size() >= minimum && c.size() <= maximum,
            String.format("must have between %d and %d elements", minimum, maximum),
            true);
  }
}
