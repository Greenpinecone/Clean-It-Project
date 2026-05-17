package com.mk.backend.ordermanagement.application.validation;

import com.mk.backend.ordermanagement.model.type.OrderStatus;
import com.mk.backend.ordermanagement.web.dto.request.CreateOrderCustomerRequest;
import com.mk.backend.ordermanagement.web.dto.request.CreateOrderItemRequest;
import com.mk.backend.ordermanagement.web.dto.request.CreateOrderRequest;
import com.mk.backend.shared.error.ConflictException;
import com.mk.backend.shared.error.FieldValidationError;
import com.mk.backend.shared.error.ValidationException;
import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class OrderValidator {

  private final Clock clock;

  OrderValidator(Clock clock) {
    this.clock = clock;
  }

  private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED_TRANSITIONS =
      Map.of(
          OrderStatus.RECEIVED,
          Set.of(OrderStatus.IN_CLEANING, OrderStatus.CANCELLED),
          OrderStatus.IN_CLEANING,
          Set.of(OrderStatus.READY_FOR_PICKUP, OrderStatus.CANCELLED),
          OrderStatus.READY_FOR_PICKUP,
          Set.of(OrderStatus.RETURNED, OrderStatus.CANCELLED),
          OrderStatus.RETURNED,
          Set.of(),
          OrderStatus.CANCELLED,
          Set.of());

  public void validateStatusChange(OrderStatus orderStatus, OrderStatus targetStatus) {
    if (orderStatus == targetStatus) {
      return;
    }

    Set<OrderStatus> allowedTargets = ALLOWED_TRANSITIONS.get(orderStatus);

    if (!allowedTargets.contains(targetStatus)) {
      throw new ConflictException(
          "Order status cannot be changed from %s to %s.".formatted(orderStatus, targetStatus));
    }
  }

  public void validateCreateOrder(CreateOrderRequest request) {
    LocalDate today = LocalDate.now(clock);
    List<FieldValidationError> errors = new ArrayList<>();

    validateCustomer(request.customer(), errors);
    validateExpectedPickupDate(request, today, errors);

    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }
  }

  private void validateExpectedPickupDate(
      CreateOrderRequest request, LocalDate today, List<FieldValidationError> errors) {

    if (request.expectedPickupDate().isBefore(today)) {
      errors.add(
          new FieldValidationError(
              "expectedPickupDate", "Expected pickup date must be today or in the future."));
      return;
    }

    LocalDate expectedPickupDate = calculateExpectedPickupDate(today, request.items());

    if (!request.expectedPickupDate().equals(expectedPickupDate)) {
      errors.add(
          new FieldValidationError(
              "expectedPickupDate",
              "Expected pickup date must be %s based on the selected item quantity."
                  .formatted(expectedPickupDate)));
    }
  }

  private LocalDate calculateExpectedPickupDate(
      LocalDate receivedDate, List<CreateOrderItemRequest> items) {
    int itemCount =
        items.stream().map(CreateOrderItemRequest::quantity).mapToInt(Integer::intValue).sum();

    int daysToAdd = Math.max(itemCount, 1);
    return receivedDate.plusDays(daysToAdd);
  }

  private void validateCustomer(
      CreateOrderCustomerRequest customer, List<FieldValidationError> errors) {

    boolean hasEmail = customer.email() != null;
    boolean hasPhoneNumber = customer.phoneNumber() != null;

    if (!hasEmail && !hasPhoneNumber) {
      errors.add(new FieldValidationError("customer", "Either email or phone number is required."));
    }
  }
}
