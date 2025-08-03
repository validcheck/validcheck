package io.github.validcheck.example.benchmark.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

/** User POJO for benchmarking validation approaches. */
public class User {
  @NotBlank(message = "First name cannot be blank")
  @Size(min = 1, max = 50, message = "First name must be between 1 and 50 characters")
  private String firstName;

  @NotBlank(message = "Last name cannot be blank")
  @Size(min = 1, max = 50, message = "Last name must be between 1 and 50 characters")
  private String lastName;

  @NotBlank(message = "Email cannot be blank")
  @Email(message = "Email must be valid")
  private String email;

  @NotNull(message = "Age cannot be null")
  @Min(value = 18, message = "Age must be at least 18")
  @Max(value = 120, message = "Age must be at most 120")
  private Integer age;

  @Past(message = "Birth date must be in the past")
  private LocalDate birthDate;

  @Pattern(regexp = "\\d{10}", message = "Phone must be exactly 10 digits")
  private String phone;

  @Valid
  @NotNull(message = "Address cannot be null")
  private Address address;

  @Valid
  @NotEmpty(message = "User must have at least one order")
  @Size(max = 100, message = "User cannot have more than 100 orders")
  private List<Order> orders;

  public User() {}

  public User(
      String firstName,
      String lastName,
      String email,
      Integer age,
      LocalDate birthDate,
      String phone,
      Address address,
      List<Order> orders) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.age = age;
    this.birthDate = birthDate;
    this.phone = phone;
    this.address = address;
    this.orders = orders;
  }

  // Getters and setters
  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Integer getAge() {
    return age;
  }

  public void setAge(Integer age) {
    this.age = age;
  }

  public LocalDate getBirthDate() {
    return birthDate;
  }

  public void setBirthDate(LocalDate birthDate) {
    this.birthDate = birthDate;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public Address getAddress() {
    return address;
  }

  public void setAddress(Address address) {
    this.address = address;
  }

  public List<Order> getOrders() {
    return orders;
  }

  public void setOrders(List<Order> orders) {
    this.orders = orders;
  }
}
