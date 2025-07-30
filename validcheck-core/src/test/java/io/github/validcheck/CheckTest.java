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
  void staticIsTrueAndFail() {
    isTrue(true, "should not fail");
    assertThatThrownBy(() -> isTrue(false, "custom error")).hasMessage("parameter custom error");
    assertThatThrownBy(() -> Check.fail("Error")).hasMessage("Error");
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
  void batchValidationSuccess() {
    var batch = batch();
    batch.check("name", "John").notNull().notEmpty();
    batch.check("age", 25).isPositive();
    batch.check("email", "john@example.com").notNull().matches(".*@.*");

    // Should not throw when all validations pass
    batch.validate();
    assertThat(batch.hasErrors()).isFalse();
  }

  @Test
  void batchValidationSingleError() {
    var batch = batch();
    batch.check("name", "John").notNull();
    batch.check("age", -5).isPositive(); // This will fail
    batch.check("email", "john@example.com").notNull();

    assertThat(batch.hasErrors()).isTrue();
    assertThatThrownBy(batch::validate)
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("Validation failed with 1 error(s)")
        .hasMessageContaining("- 'age' must be positive");
  }

  @Test
  void batchValidationMultipleErrors() {
    var batch = batch();
    batch.check("name", (String) null).notNull(); // Error 1
    batch.check("age", -5).isPositive(); // Error 2
    batch.check("email", "").notEmpty(); // Error 3

    assertThat(batch.hasErrors()).isTrue();
    assertThatThrownBy(batch::validate)
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("Validation failed with 3 error(s)")
        .hasMessageContaining("- 'name' must not be null")
        .hasMessageContaining("- 'age' must be positive")
        .hasMessageContaining("- 'email' must not be empty");
  }

  @Test
  void batchIsTrueAndIsFalse() {
    var batch = batch();
    batch.isTrue(true, "should not fail");
    batch.isFalse(false, "should not fail");
    batch.validate(); // Should not throw

    var batchWithErrors = batch();
    batchWithErrors.isTrue(false, "truth error");
    batchWithErrors.isFalse(true, "false error");

    assertThatThrownBy(batchWithErrors::validate)
        .hasMessageContaining("truth error")
        .hasMessageContaining("false error");
  }

  @Test
  void batchIncludeOtherBatch() {
    var batch1 = batch();
    batch1.check("name", (String) null).notNull();

    var batch2 = batch();
    batch2.check("age", -5).isPositive();

    var mainBatch = batch();
    mainBatch.include(batch1);
    mainBatch.include(batch2);
    mainBatch.check("email", "").notEmpty();

    assertThatThrownBy(mainBatch::validate)
        .hasMessageContaining("Validation failed with 3 error(s)")
        .hasMessageContaining("'name' must not be null")
        .hasMessageContaining("'age' must be positive")
        .hasMessageContaining("'email' must not be empty");
  }

  @Test
  void batchWithDifferentConfigurations() {
    // Test with includeActualValue = true (default behavior)
    var configWithValues = new ValidationConfig(true, true, null);
    var batchWithValues = withConfig(configWithValues).batch();
    batchWithValues.check("number", -5).isPositive();
    batchWithValues.check("text", "short").minLength(10);

    assertThatThrownBy(batchWithValues::validate)
        .hasMessageContaining("'number' must be positive, but it was -5") // Shows actual value
        .hasMessageContaining(
            "'text' must be at least 10 characters long, but it was 'short'"); // Shows actual value

    // Test with includeActualValue = false
    var configNoValues = new ValidationConfig(true, false, null);
    var batchNoValues = withConfig(configNoValues).batch();
    batchNoValues.check("number", -5).isPositive();
    batchNoValues.check("text", "short").minLength(10);

    assertThatThrownBy(batchNoValues::validate)
        .hasMessageContaining("'number' must be positive") // No actual value shown
        .hasMessageContaining("'text' must be at least 10 characters long") // No actual value shown
        .satisfies(
            ex -> {
              assertThat(ex.getMessage()).doesNotContain("-5");
              assertThat(ex.getMessage()).doesNotContain("short");
            });

    // Test with fillStackTrace = false
    var configNoStack = new ValidationConfig(false, true, 2);
    var batchNoStack = withConfig(configNoStack).batch();
    batchNoStack.check("value", (String) null).notNull();

    var exception =
        assertThatThrownBy(batchNoStack::validate).isInstanceOf(ValidationException.class);

    // Verify the exception doesn't have stack trace filled
    exception.satisfies(
        ex -> {
          var stackTrace = ex.getStackTrace();
          assertThat(stackTrace)
              .isEmpty(); // Stack trace should be empty when fillStackTrace = false
        });
  }

  @Test
  void withConfigCreation() {
    var config = new ValidationConfig(false, false, null);
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
  void checkNumericMethodsWithoutParamName() {
    // Test all numeric type overloads
    assertThat(check(5)).isInstanceOf(NumericValidator.class);
    assertThat(check(Integer.valueOf(5))).isInstanceOf(NumericValidator.class);
    assertThat(check(5L)).isInstanceOf(NumericValidator.class);
    assertThat(check(Long.valueOf(5L))).isInstanceOf(NumericValidator.class);
    assertThat(check(5.0)).isInstanceOf(NumericValidator.class);
    assertThat(check(Double.valueOf(5.0))).isInstanceOf(NumericValidator.class);
    assertThat(check(5.0f)).isInstanceOf(NumericValidator.class);
    assertThat(check(Float.valueOf(5.0f))).isInstanceOf(NumericValidator.class);
    assertThat(check(new BigDecimal("5"))).isInstanceOf(NumericValidator.class);

    // Test that numeric-specific methods are available
    check(5).isPositive().min(1).max(10);
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
