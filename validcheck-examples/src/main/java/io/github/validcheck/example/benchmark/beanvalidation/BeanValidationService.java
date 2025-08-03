package io.github.validcheck.example.benchmark.beanvalidation;

import io.github.validcheck.example.benchmark.model.User;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;

/** Bean Validation service for benchmarking. */
public class BeanValidationService {
  private static final Validator validator;

  static {
    ValidatorFactory factory =
        Validation.byDefaultProvider()
            .configure()
            .messageInterpolator(new ParameterMessageInterpolator())
            .buildValidatorFactory();
    validator = factory.getValidator();
  }

  public static void validateUser(User user) {
    Set<ConstraintViolation<User>> violations = validator.validate(user);
    if (!violations.isEmpty()) {
      StringBuilder sb = new StringBuilder("Validation failed:");
      for (ConstraintViolation<User> violation : violations) {
        sb.append("\n- ")
            .append(violation.getPropertyPath())
            .append(": ")
            .append(violation.getMessage());
      }
      throw new IllegalArgumentException(sb.toString());
    }
  }

  public static Validator getValidator() {
    return validator;
  }
}
