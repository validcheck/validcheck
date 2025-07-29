package io.github.validcheck;

import java.util.Objects;

public class ValidationConfig {
  public static final ValidationConfig DEFAULT = new ValidationConfig(true, true, null);

  final boolean fillStackTrace;
  final boolean logActualValue;
  final int logValueMaxLength;

  public ValidationConfig(
      boolean fillStackTrace, boolean logActualValue, Integer logValueMaxLength) {
    this.fillStackTrace = fillStackTrace;
    this.logActualValue = logActualValue;
    this.logValueMaxLength = Objects.requireNonNullElse(logValueMaxLength, 128);
  }
}
