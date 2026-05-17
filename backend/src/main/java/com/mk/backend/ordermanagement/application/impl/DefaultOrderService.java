package com.mk.backend.ordermanagement.application.impl;

import com.mk.backend.ordermanagement.application.*;
import com.mk.backend.ordermanagement.application.support.CleaningServiceCatalog;
import com.mk.backend.ordermanagement.application.support.ItemCodeGenerator;
import com.mk.backend.ordermanagement.application.support.OrderNumberGenerator;
import com.mk.backend.ordermanagement.application.validation.OrderValidator;
import com.mk.backend.ordermanagement.mapper.OrderMapper;
import com.mk.backend.ordermanagement.model.*;
import com.mk.backend.ordermanagement.model.entity.CleaningOrder;
import com.mk.backend.ordermanagement.model.entity.CleaningOrderItem;
import com.mk.backend.ordermanagement.model.entity.Customer;
import com.mk.backend.ordermanagement.model.type.CleaningServiceCode;
import com.mk.backend.ordermanagement.model.type.OrderStatus;
import com.mk.backend.ordermanagement.persistence.CustomerRepository;
import com.mk.backend.ordermanagement.persistence.OrderRepository;
import com.mk.backend.ordermanagement.persistence.OrderSearchResult;
import com.mk.backend.ordermanagement.persistence.ShopRepository;
import com.mk.backend.ordermanagement.web.dto.request.ChangeOrderStatusRequest;
import com.mk.backend.ordermanagement.web.dto.request.CreateOrderCustomerRequest;
import com.mk.backend.ordermanagement.web.dto.request.CreateOrderItemRequest;
import com.mk.backend.ordermanagement.web.dto.request.CreateOrderRequest;
import com.mk.backend.ordermanagement.web.dto.request.OrderSearchRequest;
import com.mk.backend.ordermanagement.web.dto.response.OrderSummaryResponse;
import com.mk.backend.shared.error.NotFoundException;
import com.mk.backend.shared.web.PageResponse;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
// Production: @Transactional(readOnly=True)
class DefaultOrderService implements OrderService {

  private final OrderRepository orderRepository;
  private final ShopRepository shopRepository;
  private final CustomerRepository customerRepository;
  private final OrderMapper orderMapper;
  private final OrderValidator orderValidator;
  private final CleaningServiceCatalog cleaningServiceCatalog;
  private final PricingService pricingService;
  private final OrderNumberGenerator orderNumberGenerator;
  private final ItemCodeGenerator itemCodeGenerator;
  private final Clock clock;

  DefaultOrderService(
      OrderRepository orderRepository,
      ShopRepository shopRepository,
      CustomerRepository customerRepository,
      OrderMapper orderMapper,
      OrderValidator orderValidator,
      CleaningServiceCatalog cleaningServiceCatalog,
      PricingService pricingService,
      OrderNumberGenerator orderNumberGenerator,
      ItemCodeGenerator itemCodeGenerator,
      Clock clock) {
    this.orderRepository = orderRepository;
    this.shopRepository = shopRepository;
    this.customerRepository = customerRepository;
    this.orderMapper = orderMapper;
    this.orderValidator = orderValidator;
    this.cleaningServiceCatalog = cleaningServiceCatalog;
    this.pricingService = pricingService;
    this.orderNumberGenerator = orderNumberGenerator;
    this.itemCodeGenerator = itemCodeGenerator;
    this.clock = clock;
  }

  @Override
  public PageResponse<OrderSummaryResponse> findOrders(OrderSearchRequest request) {
    String shopCode = request.shopCode();

    if (!shopRepository.existsByShopCode(shopCode)) {
      throw new NotFoundException("Shop with code %s was not found.".formatted(shopCode));
    }

    OrderSearchResult result =
        orderRepository.searchOrders(
            shopCode, request.query(), request.status(), request.page(), request.size());

    List<OrderSummaryResponse> items =
        result.items().stream().map(orderMapper::toSummaryResponse).toList();

    return new PageResponse<>(
        items,
        request.page(),
        request.size(),
        result.totalItems(),
        calculateTotalPages(result.totalItems(), request.size()));
  }

  @Override
  // Production: @Transactional / Optimistic Locking in general
  public OrderSummaryResponse createOrder(CreateOrderRequest request) {
    String shopCode = request.shopCode();

    if (!shopRepository.existsByShopCode(shopCode)) {
      throw new NotFoundException("Shop with code %s was not found.".formatted(shopCode));
    }

    orderValidator.validateCreateOrder(request);

    Customer customer = resolveCustomer(request.customer());
    Instant receivedAt = Instant.now(clock);
    int businessYear = receivedAt.atZone(clock.getZone()).getYear();
    List<CleaningOrderItem> items = createOrderItems(request.items(), businessYear);
    int totalPriceMinorUnits = pricingService.calculateOrderTotalMinorUnits(items);

    CleaningOrder order =
        new CleaningOrder(
            UUID.randomUUID(),
            orderNumberGenerator.nextOrderNumber(businessYear),
            customer,
            shopCode,
            OrderStatus.RECEIVED,
            receivedAt,
            request.expectedPickupDate(),
            items,
            totalPriceMinorUnits,
            null,
            null);

    CleaningOrder savedOrder = orderRepository.save(order);
    return orderMapper.toSummaryResponse(savedOrder);
  }

  private List<CleaningOrderItem> createOrderItems(
      List<CreateOrderItemRequest> itemRequests, int businessYear) {
    List<CleaningOrderItem> items = new ArrayList<>();

    for (CreateOrderItemRequest itemRequest : itemRequests) {
      List<SelectedCleaningService> selectedServices =
          createSelectedServices(itemRequest.serviceCodes());
      int itemPriceMinorUnits = pricingService.calculateItemPriceMinorUnits(selectedServices);

      for (int quantityIndex = 0; quantityIndex < itemRequest.quantity(); quantityIndex++) {
        int lineNumber = items.size() + 1;
        items.add(
            new CleaningOrderItem(
                UUID.randomUUID(),
                lineNumber,
                itemCodeGenerator.nextItemCode(businessYear),
                itemRequest.textileType(),
                itemRequest.material(),
                selectedServices,
                itemPriceMinorUnits));
      }
    }

    return items;
  }

  private List<SelectedCleaningService> createSelectedServices(
      List<CleaningServiceCode> serviceCodes) {
    List<SelectedCleaningService> selectedServices = new ArrayList<>();

    for (CleaningServiceCode serviceCode : serviceCodes) {
      selectedServices.add(cleaningServiceCatalog.selectedServiceByCode(serviceCode));
    }

    return selectedServices;
  }

  @Override
  public OrderSummaryResponse changeStatus(UUID orderId, ChangeOrderStatusRequest request) {
    CleaningOrder order =
        orderRepository
            .findById(orderId)
            .orElseThrow(
                () -> new NotFoundException("Order with id %s was not found.".formatted(orderId)));

    orderValidator.validateStatusChange(order.status(), request.targetStatus());

    Instant changedAt = Instant.now(clock);
    CleaningOrder updatedOrder = order.withStatus(request.targetStatus(), changedAt);
    CleaningOrder savedOrder = orderRepository.save(updatedOrder);

    return orderMapper.toSummaryResponse(savedOrder);
  }

  private Customer resolveCustomer(CreateOrderCustomerRequest request) {
    if (request.id() != null) {
      return customerRepository
          .findById(request.id())
          .orElseThrow(
              () ->
                  new NotFoundException(
                      "Customer with id %s was not found.".formatted(request.id())));
    }

    return customerRepository
        .findFirstByEmailOrPhoneNumber(request.email(), request.phoneNumber())
        .orElseGet(() -> createCustomer(request));
  }

  private Customer createCustomer(CreateOrderCustomerRequest request) {
    Customer customer =
        new Customer(
            UUID.randomUUID(), request.displayName(), request.email(), request.phoneNumber());

    return customerRepository.save(customer);
  }

  private int calculateTotalPages(int totalItems, int size) {
    if (totalItems == 0) {
      return 0;
    }

    return (int) Math.ceil((double) totalItems / size);
  }
}
