package io.github.validcheck;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class ValidationExceptionTest {

  @Test
  void constructorAndBasicProperties() {
    var errors = List.of("Error 1", "Error 2");
    var exception = new ValidationException("Combined message", errors);

    assertThat(exception.getMessage()).isEqualTo("Combined message");
    assertThat(exception.errors()).isEqualTo(errors);
    assertThat(exception).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void errorsIsImmutable() {
    var originalErrors = List.of("Error 1", "Error 2");
    var exception = new ValidationException("message", originalErrors);

    var returnedErrors = exception.errors();

    // Verify returned list is immutable
    assertThat(returnedErrors).isEqualTo(originalErrors);
    assertThat(returnedErrors).isNotSameAs(originalErrors);

    // Should throw UnsupportedOperationException when trying to modify
    try {
      returnedErrors.add("New error");
      assertThat(false).as("Expected UnsupportedOperationException").isTrue();
    } catch (UnsupportedOperationException e) {
      // Expected
    }
  }

  @Test
  void singleError() {
    var errors = List.of("Single error");
    var exception = new ValidationException("Single error", errors);

    assertThat(exception.getMessage()).isEqualTo("Single error");
    assertThat(exception.errors()).hasSize(1);
    assertThat(exception.errors().get(0)).isEqualTo("Single error");
  }

  @Test
  void multipleErrors() {
    var errors = List.of("First error", "Second error", "Third error");
    var exception = new ValidationException("Multiple errors occurred", errors);

    assertThat(exception.getMessage()).isEqualTo("Multiple errors occurred");
    assertThat(exception.errors()).hasSize(3);
    assertThat(exception.errors()).containsExactly("First error", "Second error", "Third error");
  }

  @Test
  void emptyErrorsList() {
    var errors = List.<String>of();
    var exception = new ValidationException("No specific errors", errors);

    assertThat(exception.getMessage()).isEqualTo("No specific errors");
    assertThat(exception.errors()).isEmpty();
  }
}
