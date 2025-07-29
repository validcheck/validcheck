package io.github.validcheck;

import static io.github.validcheck.Check.batch;
import static io.github.validcheck.Check.check;
import static io.github.validcheck.Check.isFalse;
import static io.github.validcheck.Check.isTrue;
import static io.github.validcheck.Check.withConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class CheckTest {

  @Test
  void staticIsTrue() {
    isTrue(true, "should not fail");
    assertThatThrownBy(() -> isTrue(false, "custom error")).hasMessage("parameter custom error");
  }

  @Test
  void staticIsFalse() {
    isFalse(false, "should not fail");
    assertThatThrownBy(() -> isFalse(true, "custom error")).hasMessage("parameter custom error");
  }

  @Test
  void batchCreation() {
    var validation = batch();
    assertThat(validation).isInstanceOf(BatchValidationContext.class);
    assertThat(validation.hasErrors()).isFalse();
  }

  @Test
  void withConfigCreation() {
    var config = new ValidationConfig(false, false);
    var configured = withConfig(config);
    assertThat(configured).isInstanceOf(ConfiguredCheck.class);
  }

  @Test
  void checkGenericMethods() {
    // Test generic check methods
    check("name", "value").notNull();
    check("value").notNull();

    assertThatThrownBy(() -> check("name", (String) null).notNull())
        .hasMessage("'name' must not be null");
    assertThatThrownBy(() -> check((String) null).notNull())
        .hasMessage("parameter must not be null");
  }

  @Test
  void checkStringMethods() {
    // Test that string-specific methods are available
    check("text", "hello").notEmpty().hasText().minLength(3);

    assertThat(check("text", "hello")).isInstanceOf(StringValidator.class);
  }

  @Test
  void checkNumericMethods() {
    // Test all numeric type overloads
    assertThat(check("int", 5)).isInstanceOf(NumericValidator.class);
    assertThat(check("integer", Integer.valueOf(5))).isInstanceOf(NumericValidator.class);
    assertThat(check("long", 5L)).isInstanceOf(NumericValidator.class);
    assertThat(check("longObj", Long.valueOf(5L))).isInstanceOf(NumericValidator.class);
    assertThat(check("double", 5.0)).isInstanceOf(NumericValidator.class);
    assertThat(check("doubleObj", Double.valueOf(5.0))).isInstanceOf(NumericValidator.class);
    assertThat(check("float", 5.0f)).isInstanceOf(NumericValidator.class);
    assertThat(check("floatObj", Float.valueOf(5.0f))).isInstanceOf(NumericValidator.class);
    assertThat(check("bigDecimal", new BigDecimal("5"))).isInstanceOf(NumericValidator.class);

    // Test that numeric-specific methods are available
    check("positive", 5).isPositive().min(1).max(10);
  }

  @Test
  void methodChaining() {
    // Test that all methods return validators that can be chained
    check("complex", "test@example.com")
        .notNull()
        .notEmpty()
        .hasText()
        .minLength(5)
        .matches(".*@.*");

    check("number", 42).notNull().isPositive().min(1).max(100).between(40, 50);
  }
}
