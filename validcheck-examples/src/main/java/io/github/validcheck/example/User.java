package io.github.validcheck.example;

import static io.github.validcheck.Check.check;

public record User(String name, String email, int age) {

  public User {
    check("name", name).notNull().lengthBetween(2, 50);
    check("email", email).notNull().isEmail();
    check("age", age).isNonNegative().max(120);
  }

  // Usage example
  public static void main(String[] args) {
    User user = new User("John", "john@example.com", 25); // ✓ Valid
    // User invalid = new User("", "invalid", -5);        // ✗ Throws ValidationException
  }
}
