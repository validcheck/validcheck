package io.github.validcheck.example.benchmark;

import io.github.validcheck.example.benchmark.beanvalidation.BeanValidationService;
import io.github.validcheck.example.benchmark.model.*;
import io.github.validcheck.example.benchmark.validcheck.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

/**
 * JMH benchmark comparing ValidCheck with Bean Validation.
 *
 * <p>This benchmark tests validation performance on hierarchical object structures with different
 * levels of complexity.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 3, time = 2, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 2, timeUnit = TimeUnit.SECONDS)
public class ValidationBenchmark {

  // Test data for Bean Validation (POJOs)
  private User validUser;
  private User invalidUser;

  // Test data for ValidCheck (Records)
  private AddressRecord validAddress;
  private List<OrderItemRecord> validItems;
  private List<OrderRecord> validOrders;

  @Setup
  public void setup() {
    setupBeanValidationData();
    setupValidCheckData();
  }

  private void setupBeanValidationData() {
    // Valid user with complete hierarchy
    Address address = new Address("123 Main Street", "Springfield", "IL", "62701", "USA");

    OrderItem item1 =
        new OrderItem(
            "PROD-123456",
            "Laptop Computer",
            1,
            new BigDecimal("999.99"),
            new BigDecimal("999.99"),
            "High-performance laptop");

    OrderItem item2 =
        new OrderItem(
            "PROD-789012",
            "Wireless Mouse",
            2,
            new BigDecimal("29.99"),
            new BigDecimal("59.98"),
            "Ergonomic wireless mouse");

    Order order =
        new Order(
            "ORD-12345678",
            LocalDateTime.now().minusDays(1),
            new BigDecimal("1059.97"),
            "DELIVERED",
            address,
            List.of(item1, item2),
            "Rush delivery");

    validUser =
        new User(
            "John",
            "Doe",
            "john.doe@example.com",
            30,
            LocalDate.of(1993, 5, 15),
            "5551234567",
            address,
            List.of(order));

    // Invalid user for error path testing
    invalidUser =
        new User(
            "",
            "",
            "invalid-email",
            15, // Invalid: empty names, bad email, underage
            LocalDate.now().plusDays(1),
            "123", // Invalid: future date, short phone
            null,
            List.of() // Invalid: null address, empty orders
            );
  }

  private void setupValidCheckData() {
    validAddress = new AddressRecord("123 Main Street", "Springfield", "IL", "62701", "USA");

    OrderItemRecord item1 =
        new OrderItemRecord(
            "PROD-123456",
            "Laptop Computer",
            1,
            new BigDecimal("999.99"),
            new BigDecimal("999.99"),
            "High-performance laptop");

    OrderItemRecord item2 =
        new OrderItemRecord(
            "PROD-789012",
            "Wireless Mouse",
            2,
            new BigDecimal("29.99"),
            new BigDecimal("59.98"),
            "Ergonomic wireless mouse");

    validItems = List.of(item1, item2);

    OrderRecord order =
        new OrderRecord(
            "ORD-12345678",
            LocalDateTime.now().minusDays(1),
            new BigDecimal("1059.97"),
            "DELIVERED",
            validAddress,
            validItems,
            "Rush delivery");

    validOrders = List.of(order);
  }

  // ===================== Bean Validation Benchmarks =====================

  @Benchmark
  public void beanValidation_validUser(Blackhole blackhole) {
    try {
      BeanValidationService.validateUser(validUser);
      blackhole.consume("valid");
    } catch (Exception e) {
      blackhole.consume(e);
    }
  }

  @Benchmark
  public void beanValidation_invalidUser(Blackhole blackhole) {
    try {
      BeanValidationService.validateUser(invalidUser);
      blackhole.consume("valid");
    } catch (Exception e) {
      blackhole.consume(e);
    }
  }

  // ===================== ValidCheck Benchmarks =====================

  @Benchmark
  public void validCheck_validUser(Blackhole blackhole) {
    try {
      UserRecord user =
          new UserRecord(
              "John",
              "Doe",
              "john.doe@example.com",
              30,
              LocalDate.of(1993, 5, 15),
              "5551234567",
              validAddress,
              validOrders);
      blackhole.consume(user);
    } catch (Exception e) {
      blackhole.consume(e);
    }
  }

  @Benchmark
  public void validCheck_invalidUser(Blackhole blackhole) {
    try {
      UserRecord user =
          new UserRecord(
              "", "", "invalid-email", 15, LocalDate.now().plusDays(1), "123", null, List.of());
      blackhole.consume(user);
    } catch (Exception e) {
      blackhole.consume(e);
    }
  }

  // ===================== Component-level Benchmarks =====================

  @Benchmark
  public void validCheck_address(Blackhole blackhole) {
    try {
      AddressRecord address =
          new AddressRecord("123 Main Street", "Springfield", "IL", "62701", "USA");
      blackhole.consume(address);
    } catch (Exception e) {
      blackhole.consume(e);
    }
  }

  @Benchmark
  public void validCheck_orderItem(Blackhole blackhole) {
    try {
      OrderItemRecord item =
          new OrderItemRecord(
              "PROD-123456",
              "Laptop Computer",
              1,
              new BigDecimal("999.99"),
              new BigDecimal("999.99"),
              "High-performance laptop");
      blackhole.consume(item);
    } catch (Exception e) {
      blackhole.consume(e);
    }
  }

  @Benchmark
  public void validCheck_order(Blackhole blackhole) {
    try {
      OrderRecord order =
          new OrderRecord(
              "ORD-12345678",
              LocalDateTime.now().minusDays(1),
              new BigDecimal("1059.97"),
              "DELIVERED",
              validAddress,
              validItems,
              "Rush delivery");
      blackhole.consume(order);
    } catch (Exception e) {
      blackhole.consume(e);
    }
  }

  // ===================== Batch Validation Benchmarks =====================

  @Benchmark
  public void validCheck_multipleAddresses(Blackhole blackhole) {
    try {
      for (int i = 0; i < 10; i++) {
        AddressRecord address =
            new AddressRecord("123 Main Street #" + i, "Springfield", "IL", "62701", "USA");
        blackhole.consume(address);
      }
    } catch (Exception e) {
      blackhole.consume(e);
    }
  }

  @Benchmark
  public void beanValidation_multipleAddresses(Blackhole blackhole) {
    try {
      for (int i = 0; i < 10; i++) {
        Address address = new Address("123 Main Street #" + i, "Springfield", "IL", "62701", "USA");
        BeanValidationService.getValidator().validate(address);
        blackhole.consume(address);
      }
    } catch (Exception e) {
      blackhole.consume(e);
    }
  }
}
