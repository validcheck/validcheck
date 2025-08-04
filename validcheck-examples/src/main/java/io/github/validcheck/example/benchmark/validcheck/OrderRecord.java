package io.github.validcheck.example.benchmark.validcheck;

import static io.github.validcheck.example.benchmark.validcheck.ValidationUtil.CHECK;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** Order record with ValidCheck batch validation in compact constructor. */
public record OrderRecord(
    String orderId,
    LocalDateTime orderDate,
    BigDecimal totalAmount,
    String status,
    AddressRecord shippingAddress,
    List<OrderItemRecord> items,
    String notes) {
  public OrderRecord {
    var validation = CHECK.batch();

    validation.check(orderId, "orderId").notNullOrEmpty().matches("ORD-\\d{8}");

    validation
        .check(orderDate, "orderDate")
        .notNull()
        .satisfies(date -> !date.isAfter(LocalDateTime.now()), "cannot be in the future");

    validation
        .check(totalAmount, "totalAmount")
        .notNull()
        .satisfies(amount -> amount.compareTo(new BigDecimal("0.01")) >= 0, "must be at least 0.01")
        .satisfies(
            amount -> amount.compareTo(new BigDecimal("999999.99")) <= 0,
            "cannot exceed 999999.99");

    validation
        .check(status, "status")
        .notNullOrEmpty()
        .oneOf("PENDING", "PROCESSING", "SHIPPED", "DELIVERED", "CANCELLED");

    validation.check(shippingAddress, "shippingAddress").notNull();

    validation
        .check(items, "items")
        .notNull()
        .satisfies(itemList -> !itemList.isEmpty(), "must have at least one item")
        .satisfies(itemList -> itemList.size() <= 50, "cannot have more than 50 items");

    if (notes != null) {
      validation.check(notes, "notes").maxLength(500);
    }

    validation.validate();
  }
}
