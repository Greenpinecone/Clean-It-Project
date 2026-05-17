package com.mk.backend.ordermanagement.web;

import com.mk.backend.ordermanagement.application.OrderService;
import com.mk.backend.ordermanagement.web.dto.request.CreateOrderRequest;
import com.mk.backend.ordermanagement.web.dto.response.OrderSummaryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Orders")
public class OrderController {

  private final OrderService orderService;

  OrderController(OrderService orderService) {
    this.orderService = orderService;
  }

  @PostMapping
  @Operation(summary = "Receive textiles and create a cleaning order")
  public ResponseEntity<OrderSummaryResponse> createOrder(
      @Valid @RequestBody CreateOrderRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(request));
  }
}
