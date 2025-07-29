package io.github.validcheck;

import static io.github.validcheck.Check.check;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class StringValidatorTest {

  @Test
  void empty() {
    check("empty", "").empty();
    assertThatThrownBy(() -> check("notEmpty", "text").empty())
        .hasMessage("'notEmpty' must be empty, but it was 'text'");
  }

  @Test
  void notEmpty() {
    check("text", "hello").notEmpty();
    assertThatThrownBy(() -> check("empty", "").notEmpty()).hasMessage("'empty' must not be empty");
  }

  @Test
  void hasText() {
    check("text", "hello").hasText();
    check("spaces", "  hello  ").hasText();

    assertThatThrownBy(() -> check("empty", "").hasText()).hasMessage("'empty' must have text");
    assertThatThrownBy(() -> check("whitespace", "   ").hasText())
        .hasMessage("'whitespace' must have text");
    assertThatThrownBy(() -> check("null", (String) null).hasText())
        .hasMessage("'null' must have text");
  }

  @Test
  void minLength() {
    check("text", "hello").minLength(3);
    check("exact", "hello").minLength(5);

    assertThatThrownBy(() -> check("short", "hi").minLength(5))
        .hasMessage("'short' must be at least 5 characters long, but it was 'hi'");
  }

  @Test
  void maxLength() {
    check("text", "hello").maxLength(10);
    check("exact", "hello").maxLength(5);

    assertThatThrownBy(() -> check("long", "very long text").maxLength(5))
        .hasMessage("'long' must be at most 5 characters long, but it was 'very long text'");
  }

  @Test
  void length() {
    check("exact", "hello").length(5);

    assertThatThrownBy(() -> check("short", "hi").length(5))
        .hasMessage("'short' must be exactly 5 characters long, but it was 'hi'");
    assertThatThrownBy(() -> check("long", "toolong").length(5))
        .hasMessage("'long' must be exactly 5 characters long, but it was 'toolong'");
  }

  @Test
  void matches() {
    check("email", "test@example.com").matches(".*@.*\\..*");
    check("digits", "12345").matches("\\d+");

    assertThatThrownBy(() -> check("invalid", "notanemail").matches(".*@.*\\..*"))
        .hasMessage("'invalid' must match pattern .*@.*\\..*");
  }

  @Test
  void startsWith() {
    check("prefix", "hello world").startsWith("hello");

    assertThatThrownBy(() -> check("wrong", "world hello").startsWith("hello"))
        .hasMessage("'wrong' must start with 'hello'");
  }

  @Test
  void endsWith() {
    check("suffix", "hello world").endsWith("world");

    assertThatThrownBy(() -> check("wrong", "world hello").endsWith("world"))
        .hasMessage("'wrong' must end with 'world'");
  }

  @Test
  void chainedValidations() {
    check("valid", "hello@example.com")
        .notNull()
        .notEmpty()
        .minLength(5)
        .maxLength(50)
        .matches(".*@.*")
        .startsWith("hello")
        .endsWith(".com");
  }

  @Test
  void inheritedMethods() {
    // Test that StringValidator properly inherits and chains with base methods
    check("text", "hello")
        .notNull()
        .satisfies(s -> s.contains("ell"), "must contain 'ell'")
        .notEmpty();

    // Test conditional validation
    check("conditional", "test")
        .when(true, validator -> ((StringValidator) validator).minLength(3));
    check("conditional", "test")
        .when(false, validator -> ((StringValidator) validator).minLength(10));
  }
}
