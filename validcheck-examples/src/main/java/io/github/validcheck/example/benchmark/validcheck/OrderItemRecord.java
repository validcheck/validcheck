package io.github.validcheck.example.benchmark.validcheck;

import static io.github.validcheck.example.benchmark.validcheck.ValidationUtil.CHECK;

import java.math.BigDecimal;

/** OrderItem record with ValidCheck batch validation in compact constructor. */
public record OrderItemRecord(
    String productId,
    String productName,
    Integer quantity,
    BigDecimal unitPrice,
    BigDecimal totalPrice,
    String description) {
  public OrderItemRecord {
    var validation = CHECK.batch();

    validation.check("productId", productId).notNullOrEmpty().matches("PROD-\\d{6}");

    validation.check("productName", productName).notNullOrEmpty().lengthBetween(1, 100);

    validation.check("quantity", quantity).notNull().isPositive().max(999);

    validation
        .check("unitPrice", unitPrice)
        .notNull()
        .satisfies(price -> price.compareTo(new BigDecimal("0.01")) >= 0, "must be at least 0.01")
        .satisfies(
            price -> price.compareTo(new BigDecimal("99999.99")) <= 0, "cannot exceed 99999.99");

    validation
        .check("totalPrice", totalPrice)
        .notNull()
        .satisfies(price -> price.compareTo(new BigDecimal("0.01")) >= 0, "must be at least 0.01");

    if (description != null) {
      validation.check("description", description).maxLength(200);
    }

    validation.validate();
  }
}
