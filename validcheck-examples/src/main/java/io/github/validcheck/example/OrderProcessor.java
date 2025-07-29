package io.github.validcheck.example;

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
