package com.mk.backend.ordermanagement.application;

import com.mk.backend.ordermanagement.web.dto.request.ChangeOrderStatusRequest;
import com.mk.backend.ordermanagement.web.dto.request.CreateOrderRequest;
import com.mk.backend.ordermanagement.web.dto.request.OrderSearchRequest;
import com.mk.backend.ordermanagement.web.dto.response.OrderSummaryResponse;
import com.mk.backend.shared.web.PageResponse;
import java.util.UUID;

public interface OrderService {

  PageResponse<OrderSummaryResponse> findOrders(OrderSearchRequest request);

  OrderSummaryResponse createOrder(CreateOrderRequest request);

  OrderSummaryResponse changeStatus(UUID orderId, ChangeOrderStatusRequest request);
}
