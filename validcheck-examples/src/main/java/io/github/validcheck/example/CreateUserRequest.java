package io.github.validcheck.example;

import static io.github.validcheck.Check.batch;

public record CreateUserRequest(String name, String email, Integer age, String phone) {

  public CreateUserRequest {
    var validation = batch();
    validation.check("name", name).notNullOrEmpty().lengthBetween(1, 100);
    validation.check("email", email).notNullOrEmpty().isEmail();
    validation.check("age", age).notNull().isNonNegative().max(120);
    validation
        .check("phone", phone)
        .when(
            phone != null,
            validator -> validator.satisfies(p -> p.matches("\\d{10}"), "must be 10 digits"));

    if (!validation.hasErrors()) {
      // check more business logic
      validation.fail("Business logic error");
    }

    validation.validate(); // Throws with all errors if any validation failed
  }

  // Usage example
  public static void main(String[] args) {
    var request = new CreateUserRequest("John", "john@example.com", 25, "1234567890");
    var invalid = new CreateUserRequest("", "invalid", -1, "abc"); // ✗ Throws
    // ValidationException
  }
}
