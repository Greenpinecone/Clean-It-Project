package com.mk.backend.ordermanagement.web;

import com.mk.backend.ordermanagement.application.OrderService;
import com.mk.backend.ordermanagement.web.dto.request.ChangeOrderStatusRequest;
import com.mk.backend.ordermanagement.web.dto.request.CreateOrderRequest;
import com.mk.backend.ordermanagement.web.dto.request.OrderSearchRequest;
import com.mk.backend.ordermanagement.web.dto.response.OrderSummaryResponse;
import com.mk.backend.shared.web.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

  @GetMapping
  @Operation(summary = "Search cleaning orders for a shop")
  // Production: @PreAuthorize("hasRole('SHOP_MANAGER') ...")
  public ResponseEntity<PageResponse<OrderSummaryResponse>> findOrders(
      @Valid @ParameterObject @ModelAttribute OrderSearchRequest request) {
    return ResponseEntity.ok(orderService.findOrders(request));
  }

  @PostMapping
  @Operation(summary = "Receive textiles and create a cleaning order")
  public ResponseEntity<OrderSummaryResponse> createOrder(
      @Valid @RequestBody CreateOrderRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(request));
  }

  @PatchMapping("/{orderId}/status")
  @Operation(summary = "Change the current status of a cleaning order")
  // Production: @PreAuthorize("hasRole('SHOP_MANAGER') ...")
  public ResponseEntity<OrderSummaryResponse> changeStatus(
      @PathVariable UUID orderId, @Valid @RequestBody ChangeOrderStatusRequest request) {
    return ResponseEntity.ok(orderService.changeStatus(orderId, request));
  }
}
