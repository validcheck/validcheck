# ValidCheck

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=validcheck&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=validcheck)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=validcheck&metric=coverage)](https://sonarcloud.io/summary/new_code?id=validcheck)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=validcheck&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=validcheck)

A lightweight Java runtime validation library designed for method and constructor parameter
validation without reflection or external frameworks. Perfect for Java Records and traditional
classes.

Validating input parameters is essential for reliable code. Bad inputs cause bugs, security issues,
and cryptic errors that pop up far from where the problem started. Catch these issues early at
method and constructor boundaries with clear, helpful error messages.

Constructor validation is especially powerful because it stops invalid objects from being created in
the first place. Your domain objects stay consistent from day one, making your code more predictable
and much easier to debug.

## Key Features

- **Zero dependencies** - No reflection, no external frameworks, minimal overhead
- **Fluent API** - Type-safe method chaining:
  `check("name", value).notNullOrEmpty().lengthBetween(2, 50)`
- **Fail-fast or Batch** - Stop on first error or collect all validation errors
- **Explicit validation** - Clear validation logic exactly where you need it

## Installation

Add to your Maven `pom.xml`:

```xml

<dependency>
  <groupId>io.github.validcheck</groupId>
  <artifactId>validcheck-core</artifactId>
  <version>0.0.11</version>
</dependency>
```

Or Gradle `build.gradle`:

```gradle
implementation 'io.github.validcheck:validcheck-core:0.0.11'
```

## Quick Start with Java Records

Perfect for immutable data validation in record compact constructors:

```java
import static io.github.validcheck.Check.check;

public record User(String name, String email, int age) {

  public User {
    check("name", name).notNullOrEmpty().lengthBetween(2, 50);
    check("email", email).notNullOrEmpty().isEmail();
    check("age", age).isNonNegative().max(120);
  }

  // Usage example
  public static void main(String[] args) {
    User user = new User("John", "john@example.com", 25); // ✓ Valid
    // User invalid = new User("", "invalid", -5);        // ✗ Throws ValidationException
  }
}
```

### Batch Validation in Records

```java
import static io.github.validcheck.Check.batch;

public record CreateUserRequest(String name, String email, Integer age, String phone) {

  public CreateUserRequest {
    var validation = batch();
    validation.check("name", name).notNull().lengthBetween(1, 100);
    validation.check("email", email).notNull().isEmail();
    validation.check("age", age).notNull().isNonNegative().max(120);
    validation
        .check("phone", phone)
        .when(
            phone != null,
            validator -> validator.satisfies(p -> p.matches("\\d{10}"), "must be 10 digits"));

    if (!validation.hasErrors()) {
      // check more business logic and fail manually
      validation.fail("Business logic error");
    }

    validation.validate(); // Throws with all errors if any validation failed
  }

  // Usage example
  public static void main(String[] args) {
    var request = new CreateUserRequest("John", "john@example.com", 25, "1234567890");
    // var invalid = new CreateUserRequest("", "invalid", -1, "abc"); // ✗ Throws ValidationException
  }
}
```

## Error Handling

### Single Error (Fail-Fast)

```java
import static io.github.validcheck.Check.check;

import io.github.validcheck.ValidationException;
import java.util.List;

public class ErrorHandlingExample {

  public static void main(String[] args) {
    try {
      check("age", -5).isPositive();
    } catch (ValidationException e) {
      System.out.println(e.getMessage()); // "'age' must be positive, but it was -5"
      List<String> errors = e.errors();   // Single error in list
    }
  }
}
```

### Multiple Errors (Batch)

```java
import static io.github.validcheck.Check.batch;

import io.github.validcheck.ValidationException;
import java.util.List;

public class BatchErrorExample {

  public static void main(String[] args) {
    var validation = batch();
    validation.check("name", "").notEmpty();
    validation.check("age", -1).isPositive();

    try {
      validation.validate();
    } catch (ValidationException e) {
      System.out.println(e.getMessage());
      // Output:
      // Validation failed with 2 error(s):
      // - 'name' must not be empty
      // - 'age' must be positive, but it was -1

      List<String> allErrors = e.errors(); // ["'name' must not be empty", "'age' must be positive, but it was -1"]
    }
  }
}
```

## API Reference

### Entry Points

- `Check.check(String name, T value)` - Create validator for named parameter
- `Check.check(T value)` - Create validator for unnamed parameter
- `Check.batch()` - Create batch validation context
- `Check.isTrue(boolean condition, String message)` - Assert condition
- `Check.isFalse(boolean condition, String message)` - Assert negated condition

## More Examples

### Method Parameter Validation

```java
import static io.github.validcheck.Check.check;

public class UserService {

  public void updateProfile(String userId, String email, Integer age) {
    check("userId", userId).notNullOrEmpty().lengthBetween(3, 50);
    check("email", email).notNullOrEmpty().isEmail();
    check("age", age).when(age != null, validator -> validator.isNonNegative().max(120));

    // Business logic here
    System.out.println("Profile updated for user: " + userId);
  }

  // Usage example
  public static void main(String[] args) {
    var service = new UserService();
    service.updateProfile("user123", "john@example.com", 25);
    // service.updateProfile("", "invalid", -5); // ✗ Throws ValidationException
  }
}
```

### Traditional Class Constructor

```java
import static io.github.validcheck.Check.check;

import java.math.BigDecimal;

public class BankAccount {

  private final String accountNumber;
  private final BigDecimal initialBalance;

  public BankAccount(String accountNumber, BigDecimal initialBalance) {
    check("accountNumber", accountNumber)
        .notNull()
        .satisfies(a -> a.matches("\\d{8,12}"), "must be 8-12 digits");

    check("initialBalance", initialBalance)
        .notNull()
        .satisfies(b -> b.compareTo(BigDecimal.ZERO) >= 0, "cannot be negative");

    this.accountNumber = accountNumber;
    this.initialBalance = initialBalance;
  }

  public String getAccountNumber() {
    return accountNumber;
  }

  public BigDecimal getBalance() {
    return initialBalance;
  }

  // Usage example
  public static void main(String[] args) {
    var account = new BankAccount("123456789", new BigDecimal("1000.00"));
    System.out.println("Account created: " + account.getAccountNumber());
    // var invalid = new BankAccount("abc", new BigDecimal("-100")); // ✗ Throws ValidationException
  }
}
```

### Custom Validation Logic

```java
import static io.github.validcheck.Check.check;

import java.math.BigDecimal;
import java.util.List;

// Example domain classes
class Customer {
  // Customer implementation
}

class OrderItem {

  public int getQuantity() {
    return 1;
  }
}

class Order {

  public List<OrderItem> getItems() {
    return List.of();
  }

  public Customer getCustomer() {
    return null;
  }

  public BigDecimal getTotal() {
    return BigDecimal.ZERO;
  }
}

public class OrderProcessor {

  void processOrder(Order order) {
    check("order", order)
        .notNull()
        .satisfies(o -> !o.getItems().isEmpty(), "must have at least one item")
        .satisfies(o -> o.getCustomer() != null, "must have a customer")
        .satisfies(o -> o.getTotal().compareTo(BigDecimal.ZERO) > 0, "total must be positive");

    // Additional business rules
    check("order items", order.getItems())
        .satisfies(
            items -> items.stream().allMatch(item -> item.getQuantity() > 0),
            "all items must have positive quantity");

    System.out.println("Order processed successfully");
  }

  // Usage example
  public static void main(String[] args) {
    var processor = new OrderProcessor();
    var customer = new Customer();
    var item = new OrderItem();
    var order =
        new Order() {
          public List<OrderItem> getItems() {
            return List.of(item);
          }

          public Customer getCustomer() {
            return customer;
          }

          public BigDecimal getTotal() {
            return new BigDecimal("100.00");
          }
        };
    processor.processOrder(order);
    // processor.processOrder(null); // ✗ Throws ValidationException
  }
}
```

## Real-World Example: Service Configuration

See a comprehensive example of validating complex nested configurations:

```java
// Demonstrates 3-level nested validation with batch validation,
// custom messages, and oneOf validation
public record ApplicationConfig(
        String name,
        String version,
        String environment,
        DatabaseConfig database,
        ServerConfig server,
        LoggingConfig logging) {

  public ApplicationConfig {
    check("name", name).notNullOrEmpty().matches("[a-z-]+");
    check("version", version).notNull().matches("\\d+\\.\\d+\\.\\d+");
    check("environment", environment).oneOf("development", "staging", "production");
    check("database", database).notNull();
    check("server", server).notNull();
    check("logging", logging).notNull();
  }
}
```

**Full example:**
[ServiceConfiguration.java](validcheck-examples/src/main/java/io/github/validcheck/example/config/ServiceConfiguration.java)

## Performance

ValidCheck delivers good performance comparing to traditional Bean Validation frameworks.

See detailed [Performance Benchmark Report](validcheck-examples/BENCHMARK_REPORT.md) comparing
ValidCheck vs Jakarta Bean Validation.

## Configuration

Control validation behavior with `ValidationConfig`:

```java
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
    fastCheck.check("password", password).notNullOrEmpty();

    // Or create batch with custom config
    var validation = withConfig(config).batch();
    String secret = "topsecret";
    validation.check("secret", secret).notNull();
    validation.validate();

    System.out.println("Configuration example completed successfully");
  }
}
```

### Configuration Options

- **fillStackTrace** (default: true) - Include stack trace in ValidationException
- **includeActualValue** (default: true) - Show actual values in error messages
- **actualValueMaxLength** (default: 128) - Max string length of value in the error message

**Performance tip:** Set `fillStackTrace = false` for better performance in high-throughput
scenarios.

**Security tip:** Set `includeActualValue = false` when validating sensitive data like passwords or
tokens, or taking input values untrusted sources

## Requirements

- Java 11 or higher
- Zero external dependencies

## AI Disclosure

This project was developed with AI assistance. See [AI.md](AI.md) for transparent disclosure of AI
usage in development and documentation.

## License

Licensed under the Apache License, Version 2.0. See [LICENSE](LICENSE) for details.

