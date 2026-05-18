package com.mk.backend.ordermanagement.application.validation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.mk.backend.ordermanagement.model.type.CleaningServiceCode;
import com.mk.backend.ordermanagement.model.type.OrderStatus;
import com.mk.backend.ordermanagement.model.type.TextileType;
import com.mk.backend.ordermanagement.web.dto.request.CreateOrderCustomerRequest;
import com.mk.backend.ordermanagement.web.dto.request.CreateOrderItemRequest;
import com.mk.backend.ordermanagement.web.dto.request.CreateOrderRequest;
import com.mk.backend.shared.error.ConflictException;
import com.mk.backend.shared.error.ValidationException;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.Test;

class OrderValidatorTest {

  private final Clock clock =
      Clock.fixed(Instant.parse("2026-05-18T10:00:00Z"), ZoneId.of("Europe/Berlin"));

  private final OrderValidator validator = new OrderValidator(clock);

  @Test
  void allowsValidStatusTransition() {
    assertDoesNotThrow(
        () -> validator.validateStatusChange(OrderStatus.RECEIVED, OrderStatus.IN_CLEANING));
  }

  @Test
  void rejectsInvalidStatusTransition() {
    ConflictException exception =
        assertThrows(
            ConflictException.class,
            () -> validator.validateStatusChange(OrderStatus.RETURNED, OrderStatus.IN_CLEANING));

    assertEquals(
        "Order status cannot be changed from RETURNED to IN_CLEANING.", exception.getMessage());
  }

  @Test
  void rejectsUnexpectedPickupDateWhenItDoesNotMatchItemQuantity() {
    CreateOrderRequest request =
        new CreateOrderRequest(
            "VIE-01",
            LocalDate.parse("2026-05-19"),
            new CreateOrderCustomerRequest(
                null, "Max Mustermann", "max.mustermann@example.at", null),
            List.of(
                new CreateOrderItemRequest(
                    TextileType.SHIRT,
                    2,
                    "Cotton",
                    List.of(CleaningServiceCode.STANDARD_CLEANING))));

    ValidationException exception =
        assertThrows(ValidationException.class, () -> validator.validateCreateOrder(request));

    assertEquals(1, exception.errors().size());
    assertEquals("expectedPickupDate", exception.errors().getFirst().field());
    assertEquals(
        "Expected pickup date must be 2026-05-20 based on the selected item quantity.",
        exception.errors().getFirst().message());
  }
}
