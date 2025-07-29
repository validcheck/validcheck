package io.github.validcheck;

import java.util.Collections;
import java.util.List;

public class ValidationException extends IllegalArgumentException {
  private final List<String> errors;

  ValidationException(String message, List<String> errors) {
    super(message);
    this.errors = Collections.unmodifiableList(errors);
  }

  public List<String> errors() {
    return errors;
  }
}
