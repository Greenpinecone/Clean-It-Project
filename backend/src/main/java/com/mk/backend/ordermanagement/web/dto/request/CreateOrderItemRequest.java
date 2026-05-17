package com.mk.backend.ordermanagement.web.dto.request;

import com.mk.backend.ordermanagement.model.type.CleaningServiceCode;
import com.mk.backend.ordermanagement.model.type.TextileType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record CreateOrderItemRequest(
    @NotNull(message = "Textile type is required.") TextileType textileType,
    @NotNull(message = "Quantity is required.") @Min(value = 1, message = "Quantity must be at least 1.") @Max(value = 20, message = "Quantity must not exceed 20.") Integer quantity,
    @Size(max = 80, message = "Material must not exceed 80 characters.") String material,
    @NotEmpty(message = "Select at least one service.") @Size(max = 10, message = "At most 10 services can be selected per item.") List<CleaningServiceCode> serviceCodes) {

  public CreateOrderItemRequest {
    material = material == null || material.isBlank() ? null : material.trim();

    if (serviceCodes != null) {
      serviceCodes = serviceCodes.stream().distinct().toList();
    }
  }
}
