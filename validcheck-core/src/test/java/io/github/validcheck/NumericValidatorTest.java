package io.github.validcheck;

import static io.github.validcheck.Check.check;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class NumericValidatorTest {

  @Test
  void isPositive() {
    check("positive", 5).isPositive();
    check("positive", 1.5).isPositive();
    check("positive", new BigDecimal("0.1")).isPositive();

    assertThatThrownBy(() -> check("zero", 0).isPositive())
        .hasMessage("'zero' must be positive, but it was 0");
    assertThatThrownBy(() -> check("negative", -5).isPositive())
        .hasMessage("'negative' must be positive, but it was -5");
  }

  @Test
  void isNegative() {
    check("negative", -5).isNegative();
    check("negative", -1.5).isNegative();
    check("negative", new BigDecimal("-0.1")).isNegative();

    assertThatThrownBy(() -> check("zero", 0).isNegative())
        .hasMessage("'zero' must be negative, but it was 0");
    assertThatThrownBy(() -> check("positive", 5).isNegative())
        .hasMessage("'positive' must be negative, but it was 5");
  }

  @Test
  void isZero() {
    check("zero", 0).isZero();
    check("zero", 0.0).isZero();
    check("zero", new BigDecimal("0")).isZero();

    assertThatThrownBy(() -> check("positive", 1).isZero())
        .hasMessage("'positive' must be zero, but it was 1");
    assertThatThrownBy(() -> check("negative", -1).isZero())
        .hasMessage("'negative' must be zero, but it was -1");
  }

  @Test
  void min() {
    check("valid", 10).min(5);
    check("equal", 5).min(5);
    check("double", 10.5).min(10.0);

    assertThatThrownBy(() -> check("tooSmall", 3).min(5))
        .hasMessage("'tooSmall' must be at least 5, but it was 3");
  }

  @Test
  void max() {
    check("valid", 3).max(5);
    check("equal", 5).max(5);
    check("double", 10.0).max(10.5);

    assertThatThrownBy(() -> check("tooLarge", 7).max(5))
        .hasMessage("'tooLarge' must be at most 5, but it was 7");
  }

  @Test
  void between() {
    check("valid", 5).between(1, 10);
    check("minBoundary", 1).between(1, 10);
    check("maxBoundary", 10).between(1, 10);
    check("double", 5.5).between(1.0, 10.0);

    assertThatThrownBy(() -> check("tooSmall", 0).between(1, 10))
        .hasMessage("'tooSmall' must be between 1 and 10, but it was 0");
    assertThatThrownBy(() -> check("tooLarge", 11).between(1, 10))
        .hasMessage("'tooLarge' must be between 1 and 10, but it was 11");
  }

  @Test
  void chainedValidations() {
    check("valid", 25).notNull().isPositive().min(18).max(120).between(20, 30);
  }

  @Test
  void differentNumericTypes() {
    // Integer
    check("int", 42).isPositive().min(1).max(100);

    // Long
    check("long", 42L).isPositive().min(1L).max(100L);

    // Double
    check("double", 42.5).isPositive().min(1.0).max(100.0);

    // Float
    check("float", 42.5f).isPositive().min(1.0f).max(100.0f);

    // BigDecimal
    check("bigDecimal", new BigDecimal("42.5"))
        .isPositive()
        .min(new BigDecimal("1"))
        .max(new BigDecimal("100"));
  }

  @Test
  void inheritedMethods() {
    // Test that NumericValidator properly inherits and chains with base methods
    check("number", 42).notNull().satisfies(n -> n % 2 == 0, "must be even").isPositive();

    // Test conditional validation
    boolean strict = true;
    check("conditional", 15)
        .when(strict, validator -> ((NumericValidator<Integer>) validator).max(20));
    check("conditional", 25)
        .when(!strict, validator -> ((NumericValidator<Integer>) validator).max(20));
  }

  @Test
  void isNonNegative() {
    check("positive", 5).isNonNegative();
    check("zero", 0).isNonNegative();
    check("positiveDouble", 1.5).isNonNegative();
    check("zeroDouble", 0.0).isNonNegative();
    check("bigDecimal", new BigDecimal("10.5")).isNonNegative();

    assertThatThrownBy(() -> check("negative", -1).isNonNegative())
        .hasMessage("'negative' must be non-negative, but it was -1");
    assertThatThrownBy(() -> check("negativeDouble", -0.5).isNonNegative())
        .hasMessage("'negativeDouble' must be non-negative, but it was -0.5");
  }

  @Test
  void isNonZero() {
    check("positive", 5).isNonZero();
    check("negative", -5).isNonZero();
    check("positiveDouble", 1.5).isNonZero();
    check("negativeDouble", -1.5).isNonZero();
    check("bigDecimal", new BigDecimal("0.1")).isNonZero();

    assertThatThrownBy(() -> check("zero", 0).isNonZero())
        .hasMessage("'zero' must be non-zero, but it was 0");
    assertThatThrownBy(() -> check("zeroDouble", 0.0).isNonZero())
        .hasMessage("'zeroDouble' must be non-zero, but it was 0.0");
    assertThatThrownBy(() -> check("zeroBigDecimal", new BigDecimal("0")).isNonZero())
        .hasMessage("'zeroBigDecimal' must be non-zero, but it was 0");
  }

  @Test
  void edgeCases() {
    // Test with very small positive number
    check("tiny", 0.0001).isPositive();

    // Test with very small negative number
    check("tiny", -0.0001).isNegative();

    // Test boundary conditions
    check("boundary", Integer.MAX_VALUE).isPositive();
    check("boundary", Integer.MIN_VALUE).isNegative();
  }
}
