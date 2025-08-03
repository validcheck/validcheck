package io.github.validcheck.example.benchmark.model;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/** OrderItem POJO for benchmarking validation approaches. */
public class OrderItem {
  @NotBlank(message = "Product ID cannot be blank")
  @Pattern(regexp = "PROD-\\d{6}", message = "Product ID must be in format PROD-123456")
  private String productId;

  @NotBlank(message = "Product name cannot be blank")
  @Size(min = 1, max = 100, message = "Product name must be between 1 and 100 characters")
  private String productName;

  @NotNull(message = "Quantity cannot be null")
  @Min(value = 1, message = "Quantity must be at least 1")
  @Max(value = 999, message = "Quantity cannot exceed 999")
  private Integer quantity;

  @NotNull(message = "Unit price cannot be null")
  @DecimalMin(value = "0.01", message = "Unit price must be at least 0.01")
  @DecimalMax(value = "99999.99", message = "Unit price cannot exceed 99999.99")
  private BigDecimal unitPrice;

  @NotNull(message = "Total price cannot be null")
  @DecimalMin(value = "0.01", message = "Total price must be at least 0.01")
  private BigDecimal totalPrice;

  @Size(max = 200, message = "Description cannot exceed 200 characters")
  private String description;

  public OrderItem() {}

  public OrderItem(
      String productId,
      String productName,
      Integer quantity,
      BigDecimal unitPrice,
      BigDecimal totalPrice,
      String description) {
    this.productId = productId;
    this.productName = productName;
    this.quantity = quantity;
    this.unitPrice = unitPrice;
    this.totalPrice = totalPrice;
    this.description = description;
  }

  // Getters and setters
  public String getProductId() {
    return productId;
  }

  public void setProductId(String productId) {
    this.productId = productId;
  }

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public Integer getQuantity() {
    return quantity;
  }

  public void setQuantity(Integer quantity) {
    this.quantity = quantity;
  }

  public BigDecimal getUnitPrice() {
    return unitPrice;
  }

  public void setUnitPrice(BigDecimal unitPrice) {
    this.unitPrice = unitPrice;
  }

  public BigDecimal getTotalPrice() {
    return totalPrice;
  }

  public void setTotalPrice(BigDecimal totalPrice) {
    this.totalPrice = totalPrice;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}
