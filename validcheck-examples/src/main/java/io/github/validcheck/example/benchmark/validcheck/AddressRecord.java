package io.github.validcheck.example.benchmark.validcheck;

import static io.github.validcheck.example.benchmark.validcheck.ValidationUtil.CHECK;

/** Address record with ValidCheck batch validation in compact constructor. */
public record AddressRecord(
    String street, String city, String state, String zipCode, String country) {
  public AddressRecord {
    var validation = CHECK.batch();

    validation.check(street, "street").notNullOrEmpty().lengthBetween(5, 100);

    validation.check(city, "city").notNullOrEmpty().lengthBetween(2, 50);

    validation.check(state, "state").notNullOrEmpty().length(2);

    validation.check(zipCode, "zipCode").notNullOrEmpty().matches("\\d{5}(-\\d{4})?");

    validation.check(country, "country").notNullOrEmpty().lengthBetween(2, 50);

    validation.validate();
  }
}
