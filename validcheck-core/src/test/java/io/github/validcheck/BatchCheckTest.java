package io.github.validcheck;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

public class BatchCheckTest {
  @Test
  void basic() {
    var validation = Check.batch();
    assertThat(validation.hasErrors()).isFalse();

    validation.fail("Error 1");
    validation.isFalse(true, "Error 2");

    assertThat(validation.hasErrors()).isTrue();
    assertThatThrownBy(validation::validate)
        .hasMessage("Validation failed with 2 error(s):\n- Error 1\n- Error 2");
  }
}
