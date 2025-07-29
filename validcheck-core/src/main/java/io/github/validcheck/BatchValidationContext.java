package io.github.validcheck;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BatchValidationContext extends ValidationContext {
  private final List<String> errors;

  BatchValidationContext(ValidationConfig config) {
    super(config);
    errors = new ArrayList<>();
  }

  @Override
  public void fail(String message) {
    errors.add(message);
  }

  public void validate() {
    if (!errors.isEmpty()) {
      throwException(errors);
    }
  }

  public void include(BatchValidationContext context) {
    errors.addAll(context.errors);
  }

  @Override
  String formatMessage(List<String> errors) {
    final var prefix = String.format("Validation failed with %d error(s):\n", errors.size());
    return errors.stream().map(m -> "- " + m).collect(Collectors.joining("\n", prefix, ""));
  }

  public boolean hasErrors() {
    return !errors.isEmpty();
  }

  public void isTrue(boolean truth, String message) {
    check(truth).satisfies(t -> t, message);
  }

  public void isFalse(boolean lie, String message) {
    isTrue(!lie, message);
  }
}
