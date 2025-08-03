package io.github.validcheck.example.benchmark.validcheck;

import static io.github.validcheck.example.benchmark.validcheck.ValidationUtil.CHECK;

import java.time.LocalDate;
import java.util.List;

/** User record with ValidCheck batch validation in compact constructor. */
public record UserRecord(
    String firstName,
    String lastName,
    String email,
    Integer age,
    LocalDate birthDate,
    String phone,
    AddressRecord address,
    List<OrderRecord> orders) {
  public UserRecord {
    var validation = CHECK.batch();

    validation.check("firstName", firstName).notNullOrEmpty().lengthBetween(1, 50);

    validation.check("lastName", lastName).notNullOrEmpty().lengthBetween(1, 50);

    validation.check("email", email).notNullOrEmpty().isEmail();

    validation.check("age", age).notNull().isNonNegative().min(18).max(120);

    if (birthDate != null) {
      validation
          .check("birthDate", birthDate)
          .satisfies(date -> date.isBefore(LocalDate.now()), "must be in the past");
    }

    if (phone != null) {
      validation.check("phone", phone).matches("\\d{10}");
    }

    validation.check("address", address).notNull();

    validation
        .check("orders", orders)
        .notNull()
        .satisfies(orderList -> !orderList.isEmpty(), "must have at least one order")
        .satisfies(orderList -> orderList.size() <= 100, "cannot have more than 100 orders");

    validation.validate();
  }
}
