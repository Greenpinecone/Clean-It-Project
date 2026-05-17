package com.mk.backend.ordermanagement.mapper;

import com.mk.backend.ordermanagement.model.entity.CleaningOrder;
import com.mk.backend.ordermanagement.web.dto.response.OrderSummaryResponse;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

  public OrderSummaryResponse toSummaryResponse(CleaningOrder order) {
    return new OrderSummaryResponse(
        order.id(),
        order.orderNumber(),
        order.customer().displayName(),
        order.shopCode(),
        order.status(),
        order.receivedAt(),
        order.expectedPickupDate(),
        order.itemCount(),
        order.totalPriceMinorUnits());
  }
}
