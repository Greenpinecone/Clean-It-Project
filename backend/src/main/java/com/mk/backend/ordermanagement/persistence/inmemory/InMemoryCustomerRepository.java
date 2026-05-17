package com.mk.backend.ordermanagement.persistence.inmemory;

import com.mk.backend.ordermanagement.model.entity.Customer;
import com.mk.backend.ordermanagement.persistence.CustomerRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
class InMemoryCustomerRepository implements CustomerRepository {

  private static final int MAX_SEARCH_RESULTS = 10;

  private final Map<UUID, Customer> customersById = new ConcurrentHashMap<>();

  InMemoryCustomerRepository() {
    initializeCustomers();
  }

  @Override
  public Optional<Customer> findById(UUID id) {
    return Optional.ofNullable(customersById.get(id));
  }

  @Override
  public List<Customer> search(String displayName, String email, String phoneNumber) {
    if (displayName == null && email == null && phoneNumber == null) {
      return List.of();
    }

    String displayNameSearch = toLowerCaseOrNull(displayName);
    String emailSearch = toLowerCaseOrNull(email);
    String phoneNumberSearch = toLowerCaseOrNull(phoneNumber);

    return customersById.values().stream()
        .filter(customer -> matchesIfPresent(customer.displayName(), displayNameSearch))
        .filter(customer -> matchesIfPresent(customer.email(), emailSearch))
        .filter(customer -> matchesIfPresent(customer.phoneNumber(), phoneNumberSearch))
        .sorted(Comparator.comparing(Customer::displayName))
        .limit(MAX_SEARCH_RESULTS)
        .toList();
  }

  @Override
  public Optional<Customer> findFirstByEmailOrPhoneNumber(String email, String phoneNumber) {
    String emailSearch = toLowerCaseOrNull(email);
    String phoneNumberSearch = toLowerCaseOrNull(phoneNumber);

    if (emailSearch == null && phoneNumberSearch == null) {
      return Optional.empty();
    }

    return customersById.values().stream()
        .filter(
            customer ->
                equalsIgnoreCase(customer.email(), emailSearch)
                    || equalsIgnoreCase(customer.phoneNumber(), phoneNumberSearch))
        .findFirst();
  }

  @Override
  public Customer save(Customer customer) {
    customersById.put(customer.id(), customer);
    return customer;
  }

  private void initializeCustomers() {
    save(
        new Customer(
            UUID.fromString("c42b9a02-3ce6-454a-8df2-c136b34d3660"),
            "Max Mustermann",
            "max.mustermann@example.at",
            "+49 660 0000001"));

    save(
        new Customer(
            UUID.fromString("af11469d-9dac-4c31-b636-0ac0b3bec9af"),
            "Anna Huber",
            "anna.huber@example.at",
            "+49 660 0000002"));

    save(
        new Customer(
            UUID.fromString("81a3fed6-a08e-4548-b9c0-4f5fdda7d533"),
            "Clean Office GmbH",
            "office@example.at",
            "+49 660 0000003"));
  }

  private boolean equalsIgnoreCase(String value, String searchTerm) {
    if (value == null || searchTerm == null) {
      return false;
    }

    return value.toLowerCase(Locale.ROOT).equals(searchTerm);
  }

  private boolean matchesIfPresent(String value, String searchTerm) {
    if (searchTerm == null) {
      return true;
    }

    if (value == null) {
      return false;
    }

    return value.toLowerCase(Locale.ROOT).contains(searchTerm);
  }

  private String toLowerCaseOrNull(String value) {
    return value == null ? null : value.toLowerCase(Locale.ROOT);
  }
}
