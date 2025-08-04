package io.github.validcheck;

import static io.github.validcheck.Check.batch;
import static io.github.validcheck.Check.check;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CollectionValidatorTest {

  @Test
  void notNull() {
    Check.check(List.of("a", "b"), "list").notNull();

    assertThatThrownBy(() -> check((Collection<String>) null, "list").notNull())
        .hasMessage("'list' must not be null");
  }

  @Test
  void isNull() {
    check((Collection<String>) null, "list").isNull();

    assertThatThrownBy(() -> check(List.of("a"), "list").isNull())
        .hasMessage("'list' must be null, but it was [a]");
  }

  @Test
  void empty() {
    check(Collections.emptyList(), "list").empty();
    check(Collections.emptyList()).empty();

    assertThatThrownBy(() -> check(List.of("a"), "list").empty())
        .hasMessage("'list' must be empty, but it was [a]");
  }

  @Test
  void notEmpty() {
    check(List.of("a", "b"), "list").notEmpty();

    assertThatThrownBy(() -> check(Collections.emptyList(), "list").notEmpty())
        .hasMessage("'list' must not be empty");

    assertThatThrownBy(() -> check((Collection<String>) null, "list").notEmpty())
        .hasMessage("'list' must not be empty");
  }

  @Test
  void size() {
    check(List.of("a", "b"), "list").size(2);

    assertThatThrownBy(() -> check(List.of("a"), "list").size(2))
        .hasMessage("'list' must have size 2, but it was [a]");
  }

  @Test
  void minSize() {
    check(List.of("a", "b", "c"), "list").minSize(2);
    check(List.of("a", "b"), "exact").minSize(2);

    assertThatThrownBy(() -> check(List.of("a"), "small").minSize(3))
        .hasMessage("'small' must have at least 3 elements, but it was [a]");
    assertThatThrownBy(() -> check(Collections.emptyList(), "empty").minSize(1))
        .hasMessage("'empty' must have at least 1 elements, but it was []");
  }

  @Test
  void maxSize() {
    check(List.of("a"), "list").maxSize(3);
    check(List.of("a", "b"), "exact").maxSize(2);
    check(Collections.emptyList(), "empty").maxSize(0);

    assertThatThrownBy(() -> check(List.of("a", "b", "c", "d"), "large").maxSize(2))
        .hasMessage("'large' must have at most 2 elements, but it was [a, b, c, d]");
  }

  @Test
  void sizeBetween() {
    check(List.of("a", "b", "c"), "valid").sizeBetween(2, 5);
    check(List.of("a", "b"), "minimum").sizeBetween(2, 5);
    check(List.of("a", "b", "c", "d", "e"), "maximum").sizeBetween(2, 5);
    check(List.of("a", "b", "c"), "exact").sizeBetween(3, 3);

    assertThatThrownBy(() -> check(List.of("a"), "small").sizeBetween(3, 5))
        .hasMessage("'small' must have between 3 and 5 elements, but it was [a]");
    assertThatThrownBy(
            () -> check(List.of("a", "b", "c", "d", "e", "f"), "large").sizeBetween(2, 4))
        .hasMessage("'large' must have between 2 and 4 elements, but it was [a, b, c, d, e, f]");
  }

  @Test
  void whenThen() {
    check(List.of()).satisfies(List::isEmpty, "OK");
    batch().check(List.of("")).when(true, v -> ((CollectionValidator<List<String>>) v).maxSize(0));
    assertThatThrownBy(() -> check(List.of()).whenCollection(true, c -> c.minSize(1)))
        .hasMessage("parameter must have at least 1 elements, but it was []");
    check(Set.of("X")).whenCollection(true, v -> v.satisfies(s -> !s.isEmpty(), "Error"));
  }

  @Test
  void satisfies() {
    check(List.of("a", "a"), "valid")
        .satisfies(l -> l.stream().allMatch(s -> s.equals("a")), "Error 1");
    batch().check(List.of(1, 2), "valid").satisfies(l -> l.contains(1), "Error 2");
    Check.withConfig(ValidationConfig.DEFAULT)
        .check(List.of(1, 2), "valid")
        .satisfies(l -> l.contains(2), "Error 2");
  }
}
