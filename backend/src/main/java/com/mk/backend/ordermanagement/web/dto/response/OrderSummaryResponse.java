package com.mk.backend.ordermanagement.web.dto.response;

import com.mk.backend.ordermanagement.model.type.OrderStatus;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record OrderSummaryResponse(
    UUID id,
    String orderNumber,
    String customerDisplayName,
    String shopCode,
    OrderStatus status,
    Instant receivedAt,
    LocalDate expectedPickupDate,
    int itemCount,
    int totalPriceMinorUnits) {}
