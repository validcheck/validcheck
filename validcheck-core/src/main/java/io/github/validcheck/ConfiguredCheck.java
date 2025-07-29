package io.github.validcheck;

public class ConfiguredCheck extends ValidationContext {

  ConfiguredCheck(ValidationConfig config) {
    super(config);
  }

  public void isTrue(boolean truth, String message) {
    check(truth).satisfies(t -> t, message);
  }

  public void isFalse(boolean lie, String message) {
    isTrue(!lie, message);
  }

  public BatchValidationContext batch() {
    return new BatchValidationContext(config);
  }
}
