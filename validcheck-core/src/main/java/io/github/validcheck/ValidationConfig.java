package io.github.validcheck;

public class ValidationConfig {
  final boolean fillStackTrace;
  final boolean logActualValue;

  public ValidationConfig(boolean fillStackTrace, boolean logActualValue) {
    this.fillStackTrace = fillStackTrace;
    this.logActualValue = logActualValue;
  }
}
