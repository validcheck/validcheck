package io.github.validcheck;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class ConfiguredCheckTest {

  @Test
  void constructorAndConfig() {
    var config = new ValidationConfig(false, false, null);
    var configured = new ConfiguredCheck(config);

    // Verify config is used - should not include actual value in error message
    assertThatThrownBy(() -> configured.check("test", "value").isNull())
        .hasMessage("'value' must be null"); // No actual value shown due to config
  }

  @Test
  void isTrueAndIsFalse() {
    var configured = new ConfiguredCheck(ValidationConfig.DEFAULT);

    configured.isTrue(true, "should not fail");
    configured.isFalse(false, "should not fail");

    assertThatThrownBy(() -> configured.isTrue(false, "truth failed")).hasMessage("truth failed");
    assertThatThrownBy(() -> configured.isFalse(true, "false failed")).hasMessage("false failed");
  }

  @Test
  void batchCreation() {
    var config = new ValidationConfig(false, true, null);
    var configured = new ConfiguredCheck(config);

    var batch = configured.batch();
    assertThat(batch).isInstanceOf(BatchValidationContext.class);
    assertThat(batch.hasErrors()).isFalse();

    batch.fail("Error 1");
    batch.fail("Error 2");

    assertThatThrownBy(batch::validate)
        .hasMessage(String.format("Validation failed with 2 error(s):%n- Error 1%n- Error 2"));
  }

  @Test
  void checkStringMethods() {
    var configured = new ConfiguredCheck(ValidationConfig.DEFAULT);

    // Test that string-specific methods are available
    assertThat(configured.check("hello", "text")).isInstanceOf(StringValidator.class);
    configured.check("hello", "text").notEmpty().hasText().minLength(3);

    assertThatThrownBy(() -> configured.check("", "empty").notEmpty())
        .hasMessage("'empty' must not be empty");
  }

  @Test
  void checkNumericMethods() {
    var configured = new ConfiguredCheck(ValidationConfig.DEFAULT);

    // Test all numeric type overloads work
    assertThat(configured.check(5, "int")).isInstanceOf(NumericValidator.class);
    assertThat(configured.check(Integer.valueOf(5), "integer"))
        .isInstanceOf(NumericValidator.class);
    assertThat(configured.check(5L, "long")).isInstanceOf(NumericValidator.class);
    assertThat(configured.check(Long.valueOf(5L), "longObj")).isInstanceOf(NumericValidator.class);
    assertThat(configured.check(5.0, "double")).isInstanceOf(NumericValidator.class);
    assertThat(configured.check(Double.valueOf(5.0), "doubleObj"))
        .isInstanceOf(NumericValidator.class);
    assertThat(configured.check(5.0f, "float")).isInstanceOf(NumericValidator.class);
    assertThat(configured.check(Float.valueOf(5.0f), "floatObj"))
        .isInstanceOf(NumericValidator.class);
    assertThat(configured.check(new BigDecimal("5"), "bigDecimal"))
        .isInstanceOf(NumericValidator.class);

    // Test that numeric-specific methods are available
    configured.check(5, "positive").isPositive().min(1).max(10);

    assertThatThrownBy(() -> configured.check(-5, "negative").isPositive())
        .hasMessage("'negative' must be positive, but it was -5");
  }

  @Test
  void configurationBehavior() {
    // Test with fillStackTrace = false
    var noStackConfig = new ValidationConfig(false, true, null);
    var configured = new ConfiguredCheck(noStackConfig);

    assertThatThrownBy(() -> configured.check((String) null, "test").notNull())
        .isInstanceOf(ValidationException.class)
        .hasMessage("'test' must not be null");

    // Test with includeActualValue = false
    var noValueConfig = new ValidationConfig(true, false, null);
    var configuredNoValue = new ConfiguredCheck(noValueConfig);

    assertThatThrownBy(() -> configuredNoValue.check("value", "test").isNull())
        .hasMessage("'test' must be null"); // No actual value shown
  }

  @Test
  void inheritedMethods() {
    var configured = new ConfiguredCheck(ValidationConfig.DEFAULT);

    // Test generic check methods are inherited
    configured.check("value", "generic").notNull();
    configured.check("value").notNull();

    // Test that fail method works
    assertThatThrownBy(() -> configured.fail("custom error")).hasMessage("custom error");
  }
}
