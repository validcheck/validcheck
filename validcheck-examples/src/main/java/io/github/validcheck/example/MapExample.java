package io.github.validcheck.example;

import static io.github.validcheck.Check.batch;
import static io.github.validcheck.Check.check;

import java.util.HashMap;
import java.util.Map;

/**
 * Example demonstrating Map validation using ValidCheck.
 *
 * <p>Shows how to validate maps with size constraints, key/value presence checks, and batch
 * validation for collecting multiple errors.
 */
public class MapExample {

  public static void main(String[] args) {
    System.out.println("=== Map Validation Examples ===\n");

    // Basic map validation
    basicMapValidation();

    // Configuration validation
    configurationValidation();

    // Batch validation with multiple errors
    batchMapValidation();

    System.out.println("All examples completed successfully!");
  }

  private static void basicMapValidation() {
    System.out.println("1. Basic Map Validation:");

    var headers = Map.of("Content-Type", "application/json", "Authorization", "Bearer token123");

    // Validate map is not null and not empty
    check(headers, "headers").notNull().notEmpty();

    // Validate map size
    check(headers, "headers").size(2);

    // Validate key presence
    check(headers, "headers").containsKey("Content-Type").containsKey("Authorization");

    // Validate value presence
    check(headers, "headers").containsValue("application/json");

    System.out.println("✓ Headers validation passed\n");
  }

  private static void configurationValidation() {
    System.out.println("2. Configuration Validation:");

    var config = new HashMap<String, String>();
    config.put("host", "localhost");
    config.put("port", "8080");
    config.put("database", "myapp");
    config.put("username", "admin");

    // Validate configuration completeness
    check(config, "config").notNull().minSize(3).containsAllKeys("host", "port", "database");

    // Validate no sensitive data in keys
    check(config, "config").doesNotContainKey("password");

    System.out.println("✓ Configuration validation passed\n");
  }

  private static void batchMapValidation() {
    System.out.println("3. Batch Map Validation:");

    var metadata = Map.of("version", "1.0", "author", "user");

    var validation = batch();
    validation.check(metadata, "metadata").notNull().notEmpty();
    validation.check(metadata, "metadata").minSize(2).maxSize(10);
    validation.check(metadata, "metadata").containsKey("version");
    validation.check(metadata, "metadata").containsValue("1.0");

    // This will pass since all validations are satisfied
    validation.validate();

    System.out.println("✓ Batch validation passed\n");
  }
}
