package com.mk.backend.ordermanagement.model.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.mk.backend.ordermanagement.model.type.OrderStatus;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CleaningOrderTest {

  @Test
  void setsReturnedAtWhenOrderIsReturned() {
    Instant changedAt = Instant.parse("2026-05-18T12:00:00Z");
    CleaningOrder order = createTestOrder(OrderStatus.READY_FOR_PICKUP);

    CleaningOrder updatedOrder = order.withStatus(OrderStatus.RETURNED, changedAt);

    assertEquals(OrderStatus.RETURNED, updatedOrder.status());
    assertEquals(changedAt, updatedOrder.returnedAt());
    assertNull(updatedOrder.cancelledAt());
  }

  @Test
  void setsCancelledAtWhenOrderIsCancelled() {
    Instant changedAt = Instant.parse("2026-05-18T12:00:00Z");
    CleaningOrder order = createTestOrder(OrderStatus.RECEIVED);

    CleaningOrder updatedOrder = order.withStatus(OrderStatus.CANCELLED, changedAt);

    assertEquals(OrderStatus.CANCELLED, updatedOrder.status());
    assertEquals(changedAt, updatedOrder.cancelledAt());
    assertNull(updatedOrder.returnedAt());
  }

  private CleaningOrder createTestOrder(OrderStatus status) {
    return new CleaningOrder(
        UUID.randomUUID(),
        "CI-2026-0001",
        new Customer(UUID.randomUUID(), "Max Mustermann", "max.mustermann@example.at", null),
        "VIE-01",
        status,
        Instant.parse("2026-05-18T10:00:00Z"),
        LocalDate.parse("2026-05-19"),
        List.of(),
        0,
        null,
        null);
  }
}
