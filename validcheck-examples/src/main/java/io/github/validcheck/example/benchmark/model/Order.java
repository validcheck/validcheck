package io.github.validcheck.example.benchmark.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** Order POJO for benchmarking validation approaches. */
public class Order {
  @NotBlank(message = "Order ID cannot be blank")
  @Pattern(regexp = "ORD-\\d{8}", message = "Order ID must be in format ORD-12345678")
  private String orderId;

  @NotNull(message = "Order date cannot be null")
  @PastOrPresent(message = "Order date cannot be in the future")
  private LocalDateTime orderDate;

  @NotNull(message = "Total amount cannot be null")
  @DecimalMin(value = "0.01", message = "Total amount must be at least 0.01")
  @DecimalMax(value = "999999.99", message = "Total amount cannot exceed 999999.99")
  private BigDecimal totalAmount;

  @NotBlank(message = "Status cannot be blank")
  @Pattern(
      regexp = "PENDING|PROCESSING|SHIPPED|DELIVERED|CANCELLED",
      message = "Status must be one of: PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED")
  private String status;

  @Valid
  @NotNull(message = "Shipping address cannot be null")
  private Address shippingAddress;

  @Valid
  @NotEmpty(message = "Order must have at least one item")
  @Size(max = 50, message = "Order cannot have more than 50 items")
  private List<OrderItem> items;

  @Size(max = 500, message = "Notes cannot exceed 500 characters")
  private String notes;

  public Order() {}

  public Order(
      String orderId,
      LocalDateTime orderDate,
      BigDecimal totalAmount,
      String status,
      Address shippingAddress,
      List<OrderItem> items,
      String notes) {
    this.orderId = orderId;
    this.orderDate = orderDate;
    this.totalAmount = totalAmount;
    this.status = status;
    this.shippingAddress = shippingAddress;
    this.items = items;
    this.notes = notes;
  }

  // Getters and setters
  public String getOrderId() {
    return orderId;
  }

  public void setOrderId(String orderId) {
    this.orderId = orderId;
  }

  public LocalDateTime getOrderDate() {
    return orderDate;
  }

  public void setOrderDate(LocalDateTime orderDate) {
    this.orderDate = orderDate;
  }

  public BigDecimal getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(BigDecimal totalAmount) {
    this.totalAmount = totalAmount;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Address getShippingAddress() {
    return shippingAddress;
  }

  public void setShippingAddress(Address shippingAddress) {
    this.shippingAddress = shippingAddress;
  }

  public List<OrderItem> getItems() {
    return items;
  }

  public void setItems(List<OrderItem> items) {
    this.items = items;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }
}
