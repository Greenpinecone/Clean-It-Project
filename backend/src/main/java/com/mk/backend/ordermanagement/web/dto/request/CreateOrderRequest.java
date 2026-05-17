package com.mk.backend.ordermanagement.web.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

public record CreateOrderRequest(
    @NotBlank(message = "Shop code is required.") @Size(max = 40, message = "Shop code must not exceed 40 characters.") String shopCode,
    @NotNull(message = "Expected pickup date is required.") @FutureOrPresent(message = "Expected pickup date must be today or in the future.") LocalDate expectedPickupDate,
    @Valid @NotNull(message = "Customer is required.") CreateOrderCustomerRequest customer,
    @Valid @NotEmpty(message = "At least one textile item is required.") @Size(max = 20, message = "At most 20 different textile rows are allowed.") List<CreateOrderItemRequest> items) {

  public CreateOrderRequest {
    shopCode = shopCode == null ? null : shopCode.trim();
  }
}
