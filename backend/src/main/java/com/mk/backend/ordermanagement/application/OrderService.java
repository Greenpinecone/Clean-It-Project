package com.mk.backend.ordermanagement.application;

import com.mk.backend.ordermanagement.web.dto.request.CreateOrderRequest;
import com.mk.backend.ordermanagement.web.dto.response.OrderSummaryResponse;

public interface OrderService {

  OrderSummaryResponse createOrder(CreateOrderRequest request);
}
