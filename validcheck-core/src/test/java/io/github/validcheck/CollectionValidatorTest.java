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
    Check.check("list", List.of("a", "b")).notNull();

    assertThatThrownBy(() -> check("list", (Collection<String>) null).notNull())
        .hasMessage("'list' must not be null");
  }

  @Test
  void isNull() {
    check("list", (Collection<String>) null).isNull();

    assertThatThrownBy(() -> check("list", List.of("a")).isNull())
        .hasMessage("'list' must be null, but it was [a]");
  }

  @Test
  void empty() {
    check("list", Collections.emptyList()).empty();
    check(Collections.emptyList()).empty();

    assertThatThrownBy(() -> check("list", List.of("a")).empty())
        .hasMessage("'list' must be empty, but it was [a]");
  }

  @Test
  void notEmpty() {
    check("list", List.of("a", "b")).notEmpty();

    assertThatThrownBy(() -> check("list", Collections.emptyList()).notEmpty())
        .hasMessage("'list' must not be empty");

    assertThatThrownBy(() -> check("list", (Collection<String>) null).notEmpty())
        .hasMessage("'list' must not be empty");
  }

  @Test
  void size() {
    check("list", List.of("a", "b")).size(2);

    assertThatThrownBy(() -> check("list", List.of("a")).size(2))
        .hasMessage("'list' must have size 2, but it was [a]");
  }

  @Test
  void minSize() {
    check("list", List.of("a", "b", "c")).minSize(2);
    check("exact", List.of("a", "b")).minSize(2);

    assertThatThrownBy(() -> check("small", List.of("a")).minSize(3))
        .hasMessage("'small' must have at least 3 elements, but it was [a]");
    assertThatThrownBy(() -> check("empty", Collections.emptyList()).minSize(1))
        .hasMessage("'empty' must have at least 1 elements, but it was []");
  }

  @Test
  void maxSize() {
    check("list", List.of("a")).maxSize(3);
    check("exact", List.of("a", "b")).maxSize(2);
    check("empty", Collections.emptyList()).maxSize(0);

    assertThatThrownBy(() -> check("large", List.of("a", "b", "c", "d")).maxSize(2))
        .hasMessage("'large' must have at most 2 elements, but it was [a, b, c, d]");
  }

  @Test
  void sizeBetween() {
    check("valid", List.of("a", "b", "c")).sizeBetween(2, 5);
    check("minimum", List.of("a", "b")).sizeBetween(2, 5);
    check("maximum", List.of("a", "b", "c", "d", "e")).sizeBetween(2, 5);
    check("exact", List.of("a", "b", "c")).sizeBetween(3, 3);

    assertThatThrownBy(() -> check("small", List.of("a")).sizeBetween(3, 5))
        .hasMessage("'small' must have between 3 and 5 elements, but it was [a]");
    assertThatThrownBy(
            () -> check("large", List.of("a", "b", "c", "d", "e", "f")).sizeBetween(2, 4))
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
}
