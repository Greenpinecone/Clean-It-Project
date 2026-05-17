package com.mk.backend.ordermanagement.web.dto.request;

import com.mk.backend.ordermanagement.model.type.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record ChangeOrderStatusRequest(
    @NotNull(message = "Target status is required.") OrderStatus targetStatus) {}
