package io.github.validcheck.example.benchmark.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Address POJO for benchmarking validation approaches. */
public class Address {
  @NotBlank(message = "Street cannot be blank")
  @Size(min = 5, max = 100, message = "Street must be between 5 and 100 characters")
  private String street;

  @NotBlank(message = "City cannot be blank")
  @Size(min = 2, max = 50, message = "City must be between 2 and 50 characters")
  private String city;

  @NotBlank(message = "State cannot be blank")
  @Size(min = 2, max = 2, message = "State must be exactly 2 characters")
  private String state;

  @NotBlank(message = "ZIP code cannot be blank")
  @Pattern(regexp = "\\d{5}(-\\d{4})?", message = "ZIP code must be in format 12345 or 12345-6789")
  private String zipCode;

  @NotBlank(message = "Country cannot be blank")
  @Size(min = 2, max = 50, message = "Country must be between 2 and 50 characters")
  private String country;

  public Address() {}

  public Address(String street, String city, String state, String zipCode, String country) {
    this.street = street;
    this.city = city;
    this.state = state;
    this.zipCode = zipCode;
    this.country = country;
  }

  // Getters and setters
  public String getStreet() {
    return street;
  }

  public void setStreet(String street) {
    this.street = street;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getZipCode() {
    return zipCode;
  }

  public void setZipCode(String zipCode) {
    this.zipCode = zipCode;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }
}
