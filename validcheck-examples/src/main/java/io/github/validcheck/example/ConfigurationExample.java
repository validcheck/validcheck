package io.github.validcheck.example;

import static io.github.validcheck.Check.withConfig;

import io.github.validcheck.ValidationConfig;

public class ConfigurationExample {

  public static void main(String[] args) {
    // Custom configuration
    var config =
        new ValidationConfig(
            false, // fillStackTrace - faster exceptions without stack traces
            false, // includeActualValue - hide sensitive values in error messages
            512 // actualValueMaxLength - limit the string length of value in the error message
            );

    // Use configured validation
    var fastCheck = withConfig(config);
    String password = "secret123";
    fastCheck.check("password", password).notNull().notEmpty();

    // Or create batch with custom config
    var validation = withConfig(config).batch();
    String secret = "topsecret";
    validation.check("secret", secret).notNull();
    validation.validate();

    System.out.println("Configuration example completed successfully");
  }
}
