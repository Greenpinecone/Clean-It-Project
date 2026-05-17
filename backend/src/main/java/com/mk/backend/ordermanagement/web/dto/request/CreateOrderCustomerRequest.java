package com.mk.backend.ordermanagement.web.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record CreateOrderCustomerRequest(
    UUID id,
    @NotBlank(message = "Customer name is required.") @Size(max = 120, message = "Customer name must not exceed 120 characters.") String displayName,
    @Email(message = "Email must be valid.") @Size(max = 160, message = "Email must not exceed 160 characters.") String email,
    @Size(max = 40, message = "Phone number must not exceed 40 characters.") String phoneNumber) {

  public CreateOrderCustomerRequest {
    displayName = displayName == null ? null : displayName.trim();
    email = email == null || email.isBlank() ? null : email.trim();
    phoneNumber = phoneNumber == null || phoneNumber.isBlank() ? null : phoneNumber.trim();
  }
}
