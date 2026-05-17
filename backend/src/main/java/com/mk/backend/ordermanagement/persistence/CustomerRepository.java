package com.mk.backend.ordermanagement.persistence;

import com.mk.backend.ordermanagement.model.entity.Customer;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository {

  Optional<Customer> findById(UUID id);

  List<Customer> search(String displayName, String email, String phoneNumber);

  Optional<Customer> findFirstByEmailOrPhoneNumber(String email, String phoneNumber);

  Customer save(Customer customer);
}
