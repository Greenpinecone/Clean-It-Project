package com.mk.backend.ordermanagement.web.dto.request;

import com.mk.backend.ordermanagement.model.type.OrderStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OrderSearchRequest(
    @NotBlank(message = "Shop code is required.") @Size(max = 40, message = "Shop code must not exceed 40 characters.") String shopCode,
    @Size(max = 100, message = "Search query must not exceed 100 characters.") String query,
    OrderStatus status,
    @Min(value = 0, message = "Page must be greater than or equal to 0.") Integer page,
    @Min(value = 1, message = "Size must be greater than or equal to 1.") @Max(value = 100, message = "Size must not exceed 100.") Integer size) {

  public OrderSearchRequest {
    shopCode = shopCode == null ? null : shopCode.trim();
    query = query == null || query.isBlank() ? null : query.trim();
    page = page == null ? 0 : page;
    size = size == null ? 10 : size;
  }
}
