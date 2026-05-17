package com.mk.backend.ordermanagement.model.entity;

import com.mk.backend.ordermanagement.model.type.OrderStatus;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record CleaningOrder(
    UUID id,
    String orderNumber,
    Customer customer,
    String shopCode,
    OrderStatus status,
    Instant receivedAt,
    LocalDate expectedPickupDate,
    List<CleaningOrderItem> items,
    int totalPriceMinorUnits,
    Instant returnedAt,
    Instant cancelledAt) {

  public CleaningOrder {
    items = List.copyOf(items);
  }

  public int itemCount() {
    return items.size();
  }

  public CleaningOrder withStatus(OrderStatus targetStatus, Instant changedAt) {
    Instant newReturnedAt = returnedAt;
    Instant newCancelledAt = cancelledAt;

    if (targetStatus == OrderStatus.RETURNED) {
      newReturnedAt = changedAt;
    }

    if (targetStatus == OrderStatus.CANCELLED) {
      newCancelledAt = changedAt;
    }

    return new CleaningOrder(
        id,
        orderNumber,
        customer,
        shopCode,
        targetStatus,
        receivedAt,
        expectedPickupDate,
        items,
        totalPriceMinorUnits,
        newReturnedAt,
        newCancelledAt);
  }
}
