package io.github.validcheck.example;

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

      List<String> allErrors =
          e.errors(); // ["'name' must not be empty", "'age' must be positive, but it was -1"]
    }
  }
}
