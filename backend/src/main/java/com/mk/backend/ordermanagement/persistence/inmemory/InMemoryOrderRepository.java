package com.mk.backend.ordermanagement.persistence.inmemory;

import com.mk.backend.ordermanagement.application.PricingService;
import com.mk.backend.ordermanagement.application.support.CleaningServiceCatalog;
import com.mk.backend.ordermanagement.model.*;
import com.mk.backend.ordermanagement.model.entity.CleaningOrder;
import com.mk.backend.ordermanagement.model.entity.CleaningOrderItem;
import com.mk.backend.ordermanagement.model.entity.Customer;
import com.mk.backend.ordermanagement.model.type.CleaningServiceCode;
import com.mk.backend.ordermanagement.model.type.OrderStatus;
import com.mk.backend.ordermanagement.model.type.TextileType;
import com.mk.backend.ordermanagement.persistence.OrderRepository;
import com.mk.backend.ordermanagement.persistence.OrderSearchResult;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
class InMemoryOrderRepository implements OrderRepository {

  private final Map<UUID, CleaningOrder> ordersById = new ConcurrentHashMap<>();
  private final CleaningServiceCatalog cleaningServiceCatalog;
  private final PricingService pricingService;

  InMemoryOrderRepository(
      CleaningServiceCatalog cleaningServiceCatalog, PricingService pricingService) {
    this.cleaningServiceCatalog = cleaningServiceCatalog;
    this.pricingService = pricingService;
    initializeOrders();
  }

  @Override
  public Optional<CleaningOrder> findById(UUID id) {
    return Optional.ofNullable(ordersById.get(id));
  }

  @Override
  public OrderSearchResult searchOrders(
      String shopCode, String query, OrderStatus status, int page, int size) {
    String searchTerm = toSearchTerm(query);

    List<CleaningOrder> matchingOrders =
        ordersById.values().stream()
            .filter(order -> order.shopCode().equals(shopCode))
            .filter(order -> matchesQuery(order, searchTerm))
            .filter(order -> matchesStatus(order, status))
            .sorted(Comparator.comparing(CleaningOrder::receivedAt).reversed())
            .toList();

    int totalItems = matchingOrders.size();
    int fromIndex = Math.min(page * size, totalItems);
    int toIndex = Math.min(fromIndex + size, totalItems);

    return new OrderSearchResult(matchingOrders.subList(fromIndex, toIndex), totalItems);
  }

  private void addOrder(CleaningOrder order) {
    ordersById.put(order.id(), order);
  }

  @Override
  public CleaningOrder save(CleaningOrder order) {
    ordersById.put(order.id(), order);
    return order;
  }

  private void initializeOrders() {
    Customer maxMustermann =
        new Customer(
            UUID.fromString("0d15aca4-8dc0-4db6-a91e-a56296ac0a24"),
            "Max Mustermann",
            "max.mustermann@example.at",
            "+49 660 0000001");

    Customer annaHuber =
        new Customer(
            UUID.fromString("884af841-8d4c-4668-acee-8e60c64e7bb2"),
            "Anna Huber",
            "anna.huber@example.at",
            "+49 660 0000002");

    Customer cleanOffice =
        new Customer(
            UUID.fromString("bc71edd3-a327-46bd-b19d-3085dd3ff991"),
            "Clean Office GmbH",
            "office@example.at",
            "+49 660 0000003");

    List<CleaningOrderItem> items1 =
        List.of(
            initializeItem(
                "27e7f3f3-32dd-4406-a30a-d85dcaab5742",
                1,
                "TAG-2026-00000001",
                TextileType.SHIRT,
                "Cotton",
                List.of(CleaningServiceCode.STANDARD_CLEANING, CleaningServiceCode.IRONING)),
            initializeItem(
                "07f6972f-9990-4508-9426-ed2979b6fadf",
                2,
                "TAG-2026-00000002",
                TextileType.SUIT,
                "Wool",
                List.of(CleaningServiceCode.DRY_CLEANING)));

    addOrder(
        new CleaningOrder(
            UUID.fromString("104d2c8a-d112-496a-824d-77ae7b0c90e9"),
            "CI-2026-0001",
            maxMustermann,
            "VIE-01",
            OrderStatus.RECEIVED,
            Instant.parse("2026-05-08T10:15:00Z"),
            LocalDate.parse("2026-05-10"),
            items1,
            pricingService.calculateOrderTotalMinorUnits(items1),
            null,
            null));

    List<CleaningOrderItem> items2 =
        List.of(
            initializeItem(
                "bd3cfa65-0681-4628-9e01-caa881ef5ebb",
                1,
                "TAG-2026-00000003",
                TextileType.DRESS,
                "Silk",
                List.of(CleaningServiceCode.DRY_CLEANING)));

    addOrder(
        new CleaningOrder(
            UUID.fromString("b4c0f8d2-7f91-4f2e-9b3b-2a8b9c6a1002"),
            "CI-2026-0002",
            annaHuber,
            "VIE-01",
            OrderStatus.IN_CLEANING,
            Instant.parse("2026-05-08T14:45:00Z"),
            LocalDate.parse("2026-05-11"),
            items2,
            pricingService.calculateOrderTotalMinorUnits(items2),
            null,
            null));

    List<CleaningOrderItem> items3 =
        List.of(
            initializeItem(
                "1edeb0ee-90b2-4fdd-a683-9dcfb0e6bc30",
                1,
                "TAG-2026-00000004",
                TextileType.SHIRT,
                "Cotton",
                List.of(CleaningServiceCode.STANDARD_CLEANING, CleaningServiceCode.IRONING)),
            initializeItem(
                "220d92af-337e-49cf-b58a-686aa8d40c44",
                2,
                "TAG-2026-00000005",
                TextileType.SHIRT,
                "Cotton",
                List.of(CleaningServiceCode.STANDARD_CLEANING, CleaningServiceCode.IRONING)),
            initializeItem(
                "8227caf2-d953-4dd9-8235-a83b68d3c5fc",
                3,
                "TAG-2026-00000006",
                TextileType.TROUSERS,
                "Cotton",
                List.of(CleaningServiceCode.STANDARD_CLEANING)));

    addOrder(
        new CleaningOrder(
            UUID.fromString("acb8404a-8c34-41aa-b009-8c1f896e4358"),
            "CI-2026-0003",
            cleanOffice,
            "VIE-01",
            OrderStatus.READY_FOR_PICKUP,
            Instant.parse("2026-05-09T08:20:00Z"),
            LocalDate.parse("2026-05-12"),
            items3,
            pricingService.calculateOrderTotalMinorUnits(items3),
            null,
            null));

    List<CleaningOrderItem> items4 =
        List.of(
            initializeItem(
                "b707467a-5652-49c5-a968-b685b2f0b10f",
                1,
                "TAG-2026-00000007",
                TextileType.COAT,
                "Wool",
                List.of(CleaningServiceCode.DRY_CLEANING, CleaningServiceCode.STAIN_REMOVAL)));

    addOrder(
        new CleaningOrder(
            UUID.fromString("b4c0f8d2-7f91-4f2e-9b3b-2a8b9c6a1004"),
            "CI-2026-0004",
            maxMustermann,
            "VIE-01",
            OrderStatus.RETURNED,
            Instant.parse("2026-05-07T09:30:00Z"),
            LocalDate.parse("2026-05-09"),
            items4,
            pricingService.calculateOrderTotalMinorUnits(items4),
            Instant.parse("2026-05-09T16:45:00Z"),
            null));

    List<CleaningOrderItem> items5 =
        List.of(
            initializeItem(
                "c4844ceb-39ea-4fec-886c-eadc66835519",
                1,
                "TAG-2026-00000008",
                TextileType.BLOUSE,
                "Viscose",
                List.of(CleaningServiceCode.STANDARD_CLEANING, CleaningServiceCode.IRONING)));

    addOrder(
        new CleaningOrder(
            UUID.fromString("b4c0f8d2-7f91-4f2e-9b3b-2a8b9c6a1005"),
            "CI-2026-0005",
            annaHuber,
            "VIE-01",
            OrderStatus.RECEIVED,
            Instant.parse("2026-05-09T11:10:00Z"),
            LocalDate.parse("2026-05-13"),
            items5,
            pricingService.calculateOrderTotalMinorUnits(items5),
            null,
            null));
  }

  private boolean matchesQuery(CleaningOrder order, String searchTerm) {
    if (searchTerm == null) {
      return true;
    }

    return containsSearchTerm(order.orderNumber(), searchTerm)
        || containsSearchTerm(order.customer().displayName(), searchTerm);
  }

  private boolean matchesStatus(CleaningOrder order, OrderStatus status) {
    return status == null || order.status() == status;
  }

  private boolean containsSearchTerm(String value, String searchTerm) {
    if (value == null || searchTerm == null) {
      return false;
    }

    return value.toLowerCase(Locale.ROOT).contains(searchTerm);
  }

  private String toSearchTerm(String value) {
    return value == null ? null : value.toLowerCase(Locale.ROOT);
  }

  private CleaningOrderItem initializeItem(
      String id,
      int lineNumber,
      String itemCode,
      TextileType type,
      String material,
      List<CleaningServiceCode> serviceCodes) {
    List<SelectedCleaningService> services =
        serviceCodes.stream().map(cleaningServiceCatalog::selectedServiceByCode).toList();

    return new CleaningOrderItem(
        UUID.fromString(id),
        lineNumber,
        itemCode,
        type,
        material,
        services,
        pricingService.calculateItemPriceMinorUnits(services));
  }
}
