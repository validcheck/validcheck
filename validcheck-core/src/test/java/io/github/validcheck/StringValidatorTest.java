package io.github.validcheck;

import static io.github.validcheck.Check.check;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class StringValidatorTest {

  @Test
  void empty() {
    check("", "empty").empty();
    assertThatThrownBy(() -> check("text", "notEmpty").empty())
        .hasMessage("'notEmpty' must be empty, but it was 'text'");
  }

  @Test
  void notEmpty() {
    check("hello", "text").notEmpty();
    assertThatThrownBy(() -> check("", "empty").notEmpty()).hasMessage("'empty' must not be empty");
  }

  @Test
  void notNullOrEmpty() {
    check("hello", "text").notNullOrEmpty();
    check("a", "singleChar").notNullOrEmpty();
    check("   ", "spaces").notNullOrEmpty(); // spaces are allowed

    assertThatThrownBy(() -> check((String) null, "null").notNullOrEmpty())
        .hasMessage("'null' must not be null or empty");
    assertThatThrownBy(() -> check("", "empty").notNullOrEmpty())
        .hasMessage("'empty' must not be null or empty");
  }

  @Test
  void hasText() {
    check("hello", "text").hasText();
    check("  hello  ", "spaces").hasText();

    assertThatThrownBy(() -> check("", "empty").hasText()).hasMessage("'empty' must have text");
    assertThatThrownBy(() -> check("   ", "whitespace").hasText())
        .hasMessage("'whitespace' must have text");
    assertThatThrownBy(() -> check((String) null, "null").hasText())
        .hasMessage("'null' must have text");
  }

  @Test
  void minLength() {
    check("hello", "text").minLength(3);
    check("hello", "exact").minLength(5);

    assertThatThrownBy(() -> check("hi", "short").minLength(5))
        .hasMessage("'short' must be at least 5 characters long, but it was 'hi'");
  }

  @Test
  void maxLength() {
    check("hello", "text").maxLength(10);
    check("hello", "exact").maxLength(5);

    assertThatThrownBy(() -> check("very long text", "long").maxLength(5))
        .hasMessage("'long' must be at most 5 characters long, but it was 'very long text'");
  }

  @Test
  void length() {
    check("hello", "exact").length(5);

    assertThatThrownBy(() -> check("hi", "short").length(5))
        .hasMessage("'short' must be exactly 5 characters long, but it was 'hi'");
    assertThatThrownBy(() -> check("toolong", "long").length(5))
        .hasMessage("'long' must be exactly 5 characters long, but it was 'toolong'");
  }

  @Test
  void matches() {
    check("test@example.com", "email").matches(".*@.*\\..*");
    check("12345", "digits").matches("\\d+");

    assertThatThrownBy(() -> check("notanemail", "invalid").matches(".*@.*\\..*"))
        .hasMessage("'invalid' must match pattern .*@.*\\..*, but it was 'notanemail'");
  }

  @Test
  void startsWith() {
    check("hello world", "prefix").startsWith("hello");

    assertThatThrownBy(() -> check("world hello", "wrong").startsWith("hello"))
        .hasMessage("'wrong' must start with 'hello', but it was 'world hello'");
  }

  @Test
  void endsWith() {
    check("hello world", "suffix").endsWith("world");

    assertThatThrownBy(() -> check("world hello", "wrong").endsWith("world"))
        .hasMessage("'wrong' must end with 'world', but it was 'world hello'");
  }

  @Test
  void chainedValidations() {
    check("hello@example.com", "valid")
        .notNull()
        .notEmpty()
        .minLength(5)
        .maxLength(50)
        .matches(".*@.*")
        .startsWith("hello")
        .endsWith(".com");
  }

  @Test
  void lengthBetween() {
    check("hello", "valid").lengthBetween(3, 10);
    check("hello", "exact").lengthBetween(5, 5);
    check("hello", "minimum").lengthBetween(5, 10);
    check("hello", "maximum").lengthBetween(3, 5);

    assertThatThrownBy(() -> check("hi", "short").lengthBetween(5, 10))
        .hasMessage("'short' must be between 5 and 10 characters long, but it was 'hi'");
    assertThatThrownBy(() -> check("very long text", "long").lengthBetween(3, 8))
        .hasMessage("'long' must be between 3 and 8 characters long, but it was 'very long text'");
  }

  @Test
  void isEmail() {
    check("test@example.com", "valid").isEmail();
    check("a@b.co", "singleLocalPart").isEmail();
    check("user.name+tag@example-domain.co.uk", "long").isEmail();

    check("user123@example.com", "numberLocal").isEmail();
    check("a.b+c@example.com", "dotAndPlus").isEmail();
    check("user@example123.com", "numberDomain").isEmail();

    assertThatThrownBy(() -> check("testexample.com", "noAt").isEmail())
        .hasMessage("'noAt' must be a valid email address, but it was 'testexample.com'");
    assertThatThrownBy(() -> check("@example.com", "startAt").isEmail())
        .hasMessage("'startAt' must be a valid email address, but it was '@example.com'");
    assertThatThrownBy(() -> check("test@", "endAt").isEmail())
        .hasMessage("'endAt' must be a valid email address, but it was 'test@'");
    assertThatThrownBy(() -> check("", "noChars").isEmail())
        .hasMessage("'noChars' must be a valid email address, but it was ''");
    assertThatThrownBy(() -> check(".a@testexample.com", "startWithDot").isEmail())
        .isInstanceOf(ValidationException.class);
    assertThatThrownBy(() -> check("a.@testexample.com", "endWithDot").isEmail())
        .isInstanceOf(ValidationException.class);
    assertThatThrownBy(() -> check("b..a@testexample.com", "consecutiveDots1").isEmail())
        .isInstanceOf(ValidationException.class);
    assertThatThrownBy(() -> check("a@testexample..com", "consecutiveDots2").isEmail())
        .isInstanceOf(ValidationException.class);

    // ReDoS protection test - very long input should fail quickly
    String longEmail = "a".repeat(400) + "@example.com";
    assertThatThrownBy(() -> check(longEmail, "tooLong").isEmail())
        .hasMessageStartingWith("'tooLong' must be a valid email address, but it was 'aa");
  }

  @Test
  void isBlank() {
    check((String) null, "null").isBlank();
    check("", "empty").isBlank();
    check("   ", "spaces").isBlank();
    check("\t\t", "tabs").isBlank();
    check(" \t \n ", "mixed").isBlank();

    assertThatThrownBy(() -> check("hello", "text").isBlank())
        .hasMessage("'text' must be blank, but it was 'hello'");
    assertThatThrownBy(() -> check(" hello ", "textWithSpaces").isBlank())
        .hasMessage("'textWithSpaces' must be blank, but it was ' hello '");
  }

  @Test
  void inheritedMethods() {
    // Test that StringValidator properly inherits and chains with base methods
    check("hello", "text")
        .notNull()
        .satisfies(s -> s.contains("ell"), "must contain 'ell'")
        .notEmpty();

    // Test conditional validation
    check("test", "conditional").whenString(true, validator -> validator.minLength(3));
    check("test", "conditional").whenString(false, validator -> validator.minLength(10));

    check("test").whenString(false, validator -> validator.minLength(10));
    check("test", "conditional")
        .when(false, validator -> ((StringValidator) validator).minLength(10));
  }
}
