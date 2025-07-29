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
    assertThatThrownBy(() -> configured.check("value", "test").isNull())
        .hasMessage("'value' must be null"); // No actual value shown due to config
  }

  @Test
  void isTrueAndIsFalse() {
    ;
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
  }

  @Test
  void checkStringMethods() {
    ;
    var configured = new ConfiguredCheck(ValidationConfig.DEFAULT);

    // Test that string-specific methods are available
    assertThat(configured.check("text", "hello")).isInstanceOf(StringValidator.class);
    configured.check("text", "hello").notEmpty().hasText().minLength(3);

    assertThatThrownBy(() -> configured.check("empty", "").notEmpty())
        .hasMessage("'empty' must not be empty");
  }

  @Test
  void checkNumericMethods() {
    ;
    var configured = new ConfiguredCheck(ValidationConfig.DEFAULT);

    // Test all numeric type overloads work
    assertThat(configured.check("int", 5)).isInstanceOf(NumericValidator.class);
    assertThat(configured.check("integer", Integer.valueOf(5)))
        .isInstanceOf(NumericValidator.class);
    assertThat(configured.check("long", 5L)).isInstanceOf(NumericValidator.class);
    assertThat(configured.check("longObj", Long.valueOf(5L))).isInstanceOf(NumericValidator.class);
    assertThat(configured.check("double", 5.0)).isInstanceOf(NumericValidator.class);
    assertThat(configured.check("doubleObj", Double.valueOf(5.0)))
        .isInstanceOf(NumericValidator.class);
    assertThat(configured.check("float", 5.0f)).isInstanceOf(NumericValidator.class);
    assertThat(configured.check("floatObj", Float.valueOf(5.0f)))
        .isInstanceOf(NumericValidator.class);
    assertThat(configured.check("bigDecimal", new BigDecimal("5")))
        .isInstanceOf(NumericValidator.class);

    // Test that numeric-specific methods are available
    configured.check("positive", 5).isPositive().min(1).max(10);

    assertThatThrownBy(() -> configured.check("negative", -5).isPositive())
        .hasMessage("'negative' must be positive, but it was -5");
  }

  @Test
  void configurationBehavior() {
    // Test with fillStackTrace = false
    var noStackConfig = new ValidationConfig(false, true, null);
    var configured = new ConfiguredCheck(noStackConfig);

    assertThatThrownBy(() -> configured.check("test", (String) null).notNull())
        .isInstanceOf(ValidationException.class)
        .hasMessage("'test' must not be null");

    // Test with logActualValue = false
    var noValueConfig = new ValidationConfig(true, false, null);
    var configuredNoValue = new ConfiguredCheck(noValueConfig);

    assertThatThrownBy(() -> configuredNoValue.check("test", "value").isNull())
        .hasMessage("'test' must be null"); // No actual value shown
  }

  @Test
  void inheritedMethods() {
    ;
    var configured = new ConfiguredCheck(ValidationConfig.DEFAULT);

    // Test generic check methods are inherited
    configured.check("generic", "value").notNull();
    configured.check("value").notNull();

    // Test that fail method works
    assertThatThrownBy(() -> configured.fail("custom error")).hasMessage("custom error");
  }
}
