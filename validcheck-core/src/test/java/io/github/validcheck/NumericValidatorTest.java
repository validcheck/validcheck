package io.github.validcheck;

import static io.github.validcheck.Check.check;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class NumericValidatorTest {

  @Test
  void isPositive() {
    check(5, "positive").isPositive();
    check(1.5, "positive").isPositive();
    check(new BigDecimal("0.1"), "positive").isPositive();

    assertThatThrownBy(() -> check(0, "zero").isPositive())
        .hasMessage("'zero' must be positive, but it was 0");
    assertThatThrownBy(() -> check(-5, "negative").isPositive())
        .hasMessage("'negative' must be positive, but it was -5");
  }

  @Test
  void isNegative() {
    check(-5, "negative").isNegative();
    check(-1.5, "negative").isNegative();
    check(new BigDecimal("-0.1"), "negative").isNegative();

    assertThatThrownBy(() -> check(0, "zero").isNegative())
        .hasMessage("'zero' must be negative, but it was 0");
    assertThatThrownBy(() -> check(5, "positive").isNegative())
        .hasMessage("'positive' must be negative, but it was 5");
  }

  @Test
  void isZero() {
    check(0, "zero").isZero();
    check(0.0, "zero").isZero();
    check(new BigDecimal("0"), "zero").isZero();

    assertThatThrownBy(() -> check(1, "positive").isZero())
        .hasMessage("'positive' must be zero, but it was 1");
    assertThatThrownBy(() -> check(-1, "negative").isZero())
        .hasMessage("'negative' must be zero, but it was -1");
  }

  @Test
  void min() {
    check(10, "valid").min(5);
    check(5, "equal").min(5);
    check(10.5, "double").min(10.0);

    assertThatThrownBy(() -> check(3, "tooSmall").min(5))
        .hasMessage("'tooSmall' must be at least 5, but it was 3");
  }

  @Test
  void max() {
    check(3, "valid").max(5);
    check(5, "equal").max(5);
    check(10.0, "double").max(10.5);

    assertThatThrownBy(() -> check(7, "tooLarge").max(5))
        .hasMessage("'tooLarge' must be at most 5, but it was 7");
  }

  @Test
  void between() {
    check(5, "valid").between(1, 10);
    check(1, "minBoundary").between(1, 10);
    check(10, "maxBoundary").between(1, 10);
    check(5.5, "double").between(1.0, 10.0);

    assertThatThrownBy(() -> check(0, "tooSmall").between(1, 10))
        .hasMessage("'tooSmall' must be between 1 and 10, but it was 0");
    assertThatThrownBy(() -> check(11, "tooLarge").between(1, 10))
        .hasMessage("'tooLarge' must be between 1 and 10, but it was 11");
  }

  @Test
  void chainedValidations() {
    check(25, "valid").notNull().isPositive().min(18).max(120).between(20, 30);
  }

  @Test
  void differentNumericTypes() {
    // Integer
    check(42, "int").isPositive().min(1).max(100);

    // Long
    check(42L, "long").isPositive().min(1L).max(100L);

    // Double
    check(42.5, "double").isPositive().min(1.0).max(100.0);

    // Float
    check(42.5f, "float").isPositive().min(1.0f).max(100.0f);

    // BigDecimal
    check(new BigDecimal("42.5"), "bigDecimal")
        .isPositive()
        .min(new BigDecimal("1"))
        .max(new BigDecimal("100"));
  }

  @Test
  void inheritedMethods() {
    // Test that NumericValidator properly inherits and chains with base methods
    check(42, "number").notNull().satisfies(n -> n % 2 == 0, "must be even").isPositive();

    // Test conditional validation
    boolean strict = true;
    check(15, "conditional").whenNumeric(strict, validator -> validator.max(20));
    check(25, "conditional")
        .when(!strict, validator -> ((NumericValidator<Integer>) validator).max(20));
  }

  @Test
  void isNonNegative() {
    check(5, "positive").isNonNegative();
    check(0, "zero").isNonNegative();
    check(1.5, "positiveDouble").isNonNegative();
    check(0.0, "zeroDouble").isNonNegative();
    check(new BigDecimal("10.5"), "bigDecimal").isNonNegative();

    assertThatThrownBy(() -> check(-1, "negative").isNonNegative())
        .hasMessage("'negative' must be non-negative, but it was -1");
    assertThatThrownBy(() -> check(-0.5, "negativeDouble").isNonNegative())
        .hasMessage("'negativeDouble' must be non-negative, but it was -0.5");
  }

  @Test
  void isNonZero() {
    check(5, "positive").isNonZero();
    check(-5, "negative").isNonZero();
    check(1.5, "positiveDouble").isNonZero();
    check(-1.5, "negativeDouble").isNonZero();
    check(new BigDecimal("0.1"), "bigDecimal").isNonZero();

    assertThatThrownBy(() -> check(0, "zero").isNonZero())
        .hasMessage("'zero' must be non-zero, but it was 0");
    assertThatThrownBy(() -> check(0.0, "zeroDouble").isNonZero())
        .hasMessage("'zeroDouble' must be non-zero, but it was 0.0");
    assertThatThrownBy(() -> check(new BigDecimal("0"), "zeroBigDecimal").isNonZero())
        .hasMessage("'zeroBigDecimal' must be non-zero, but it was 0");
  }

  @Test
  void edgeCases() {
    // Test with very small positive number
    check(0.0001, "tiny").isPositive();

    // Test with very small negative number
    check(-0.0001, "tiny").isNegative();

    // Test boundary conditions
    check(Integer.MAX_VALUE, "boundary").isPositive();
    check(Integer.MIN_VALUE, "boundary").isNegative();
  }
}
