package com.mk.backend.ordermanagement.web;

import com.mk.backend.ordermanagement.application.CustomerService;
import com.mk.backend.ordermanagement.web.dto.request.CustomerSearchRequest;
import com.mk.backend.ordermanagement.web.dto.response.CustomerSearchResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/customers")
@Tag(name = "Customers")
public class CustomerController {

  private final CustomerService customerService;

  CustomerController(CustomerService customerService) {
    this.customerService = customerService;
  }

  @GetMapping
  @Operation(summary = "Search existing customers by name, email, or phone number")
  public ResponseEntity<List<CustomerSearchResponse>> searchCustomers(
      @Valid @ParameterObject @ModelAttribute CustomerSearchRequest request) {
    return ResponseEntity.ok(customerService.searchCustomers(request));
  }
}
