package io.github.validcheck;

import java.util.Objects;

public class ValidationConfig {
  public static final ValidationConfig DEFAULT = new ValidationConfig(true, true, null);

  final boolean fillStackTrace;
  final boolean includeActualValue;
  final int actualValueMaxLength;

  public ValidationConfig(
      boolean fillStackTrace, boolean includeActualValue, Integer actualValueMaxLength) {
    this.fillStackTrace = fillStackTrace;
    this.includeActualValue = includeActualValue;
    this.actualValueMaxLength = Objects.requireNonNullElse(actualValueMaxLength, 128);
  }
}
