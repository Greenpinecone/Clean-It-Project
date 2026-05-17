package com.mk.backend.ordermanagement.application.impl;

import com.mk.backend.ordermanagement.application.CustomerService;
import com.mk.backend.ordermanagement.mapper.CustomerMapper;
import com.mk.backend.ordermanagement.persistence.CustomerRepository;
import com.mk.backend.ordermanagement.web.dto.request.CustomerSearchRequest;
import com.mk.backend.ordermanagement.web.dto.response.CustomerSearchResponse;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
class DefaultCustomerService implements CustomerService {

  private final CustomerRepository customerRepository;
  private final CustomerMapper customerMapper;

  DefaultCustomerService(CustomerRepository customerRepository, CustomerMapper customerMapper) {
    this.customerRepository = customerRepository;
    this.customerMapper = customerMapper;
  }

  @Override
  public List<CustomerSearchResponse> searchCustomers(CustomerSearchRequest request) {
    if (!request.hasSearchTerm()) {
      return List.of();
    }

    return customerRepository
        .search(request.displayName(), request.email(), request.phoneNumber())
        .stream()
        .map(customerMapper::toSearchResponse)
        .toList();
  }
}
