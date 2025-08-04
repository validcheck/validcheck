package io.github.validcheck;

import static io.github.validcheck.Check.check;
import static io.github.validcheck.Check.withConfig;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class ValueValidatorTest {

  @Test
  void basic() {
    check("value").notNull();
    check("value", "name").notNull();
    assertThatThrownBy(() -> check((String) null).notNull())
        .hasMessage("parameter must not be null");
    assertThatThrownBy(() -> check((String) null, "name").notNull())
        .hasMessage("'name' must not be null");

    check((String) null).isNull();
    check((String) null, "name").isNull();
    assertThatThrownBy(() -> check("value").isNull())
        .hasMessage("parameter must be null, but it was 'value'");
    assertThatThrownBy(() -> check("value", "name").isNull())
        .hasMessage("'name' must be null, but it was 'value'");

    assertThatThrownBy(
            () ->
                check(new Object(), "name")
                    .satisfies(o -> o.hashCode() == 0, "must have non-zero hashCode"))
        .hasMessage("'name' must have non-zero hashCode");
    assertThatThrownBy(
            () ->
                check(new Object())
                    .satisfies(o -> o.hashCode() == 0, "must have non-zero hashCode"))
        .hasMessage("parameter must have non-zero hashCode")
        .matches(e -> ((ValidationException) e).errors().size() == 1);

    check("A").oneOf("A", "B", "C").notNull();
    assertThatThrownBy(() -> check("D").oneOf("A", "B", "C", null))
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
