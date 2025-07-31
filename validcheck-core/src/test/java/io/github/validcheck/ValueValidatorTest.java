package io.github.validcheck;

import static io.github.validcheck.Check.check;
import static io.github.validcheck.Check.withConfig;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class ValueValidatorTest {

  @Test
  void basic() {
    check("value").notNull();
    check("name", "value").notNull();
    assertThatThrownBy(() -> check((String) null).notNull())
        .hasMessage("parameter must not be null");
    assertThatThrownBy(() -> check("name", (String) null).notNull())
        .hasMessage("'name' must not be null");

    check((String) null).isNull();
    check("name", (String) null).isNull();
    assertThatThrownBy(() -> check("value").isNull())
        .hasMessage("parameter must be null, but it was 'value'");
    assertThatThrownBy(() -> check("name", "value").isNull())
        .hasMessage("'name' must be null, but it was 'value'");

    assertThatThrownBy(
            () ->
                check("name", new Object())
                    .satisfies(o -> o.hashCode() == 0, "must have non-zero hashCode"))
        .hasMessage("'name' must have non-zero hashCode");
    assertThatThrownBy(
            () ->
                check(new Object())
                    .satisfies(o -> o.hashCode() == 0, "must have non-zero hashCode"))
        .hasMessage("parameter must have non-zero hashCode")
        .matches(e -> ((ValidationException) e).errors().size() == 1);

    check("A").oneOf(List.of("A", "B", "C")).notNull();
    assertThatThrownBy(
            () -> check("D").oneOf(Stream.of("A", "B", "C", null).collect(Collectors.toList())))
        .hasMessage("parameter must be one of [A, B, C, null], but it was 'D'");
  }

  @Test
  void whenThen() {
    check("string").when(false, ValueValidator::isNull);
    check("string").when(true, ValueValidator::notNull);
    assertThatThrownBy(() -> check("string").when(true, ValueValidator::isNull))
        .hasMessage("parameter must be null, but it was 'string'");
  }

  @Test
  void longValue() {
    final var configuredCheck = withConfig(new ValidationConfig(true, true, 4));
    assertThatThrownBy(() -> configuredCheck.check("s".repeat(512)).isNull())
        .hasMessage("parameter must be null, but it was 'ssss...'");

    assertThatThrownBy(() -> configuredCheck.check(1_000_000_000_000L).isNull())
        .hasMessage("parameter must be null, but it was 1000...");
  }
}
