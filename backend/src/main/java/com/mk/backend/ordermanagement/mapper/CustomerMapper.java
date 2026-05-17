package com.mk.backend.ordermanagement.mapper;

import com.mk.backend.ordermanagement.model.entity.Customer;
import com.mk.backend.ordermanagement.web.dto.response.CustomerSearchResponse;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

  public CustomerSearchResponse toSearchResponse(Customer customer) {
    return new CustomerSearchResponse(
        customer.id(), customer.displayName(), customer.email(), customer.phoneNumber());
  }
}
