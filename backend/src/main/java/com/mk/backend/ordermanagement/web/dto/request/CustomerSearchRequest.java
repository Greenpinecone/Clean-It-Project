package com.mk.backend.ordermanagement.web.dto.request;

import jakarta.validation.constraints.Size;

public record CustomerSearchRequest(
    @Size(max = 120, message = "Customer name must not exceed 120 characters.") String displayName,
    @Size(max = 160, message = "Email must not exceed 160 characters.") String email,
    @Size(max = 40, message = "Phone number must not exceed 40 characters.") String phoneNumber) {

  public CustomerSearchRequest {
    displayName = displayName == null || displayName.isBlank() ? null : displayName.trim();
    email = email == null || email.isBlank() ? null : email.trim();
    phoneNumber = phoneNumber == null || phoneNumber.isBlank() ? null : phoneNumber.trim();
  }

  public boolean hasSearchTerm() {
    return displayName != null || email != null || phoneNumber != null;
  }
}
