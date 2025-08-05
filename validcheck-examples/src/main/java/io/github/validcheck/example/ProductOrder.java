package io.github.validcheck.example;

import static io.github.validcheck.Check.batch;

import java.math.BigDecimal;
import java.util.List;

public record ProductOrder(
    String productId, Integer quantity, BigDecimal price, List<String> tags) {

  public ProductOrder {
    batch()
        .check(productId, "productId", v -> v.notNull().matches("PROD-\\d+"))
        .check(quantity, "quantity", v -> v.notNull().isPositive().max(1000))
        .check(price, "price", v -> v.notNull().isPositive().max(new BigDecimal("10000")))
        .check(tags, "tags", v -> v.notNull().minSize(1).maxSize(5))
        .isTrue(
            price.multiply(new BigDecimal(quantity)).compareTo(new BigDecimal("50000")) <= 0,
            "total order value cannot exceed $50,000")
        .validate(); // Collects all errors and throws if any validation failed
  }

  // Usage example
  public static void main(String[] args) {
    var order =
        new ProductOrder("PROD-123", 5, new BigDecimal("99.99"), List.of("electronics", "gadgets"));
    // var invalid = new ProductOrder("", 0, BigDecimal.ZERO, List.of()); // ✗ Throws
    // ValidationException with all errors
  }
}
