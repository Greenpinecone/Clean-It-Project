package com.mk.backend.ordermanagement.application.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.mk.backend.ordermanagement.model.SelectedCleaningService;
import com.mk.backend.ordermanagement.model.entity.CleaningOrderItem;
import com.mk.backend.ordermanagement.model.type.CleaningServiceCode;
import com.mk.backend.ordermanagement.model.type.TextileType;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class SimplePricingServiceTest {

  private final SimplePricingService pricingService = new SimplePricingService();

  @Test
  void calculatesItemPriceFromSelectedServices() {
    int price =
        pricingService.calculateItemPriceMinorUnits(
            List.of(
                new SelectedCleaningService(
                    CleaningServiceCode.STANDARD_CLEANING, "Standard cleaning", 990),
                new SelectedCleaningService(CleaningServiceCode.IRONING, "Ironing", 490)));

    assertEquals(1480, price);
  }

  @Test
  void calculatesOrderTotalFromItemPrices() {
    CleaningOrderItem shirt =
        new CleaningOrderItem(
            UUID.randomUUID(),
            1,
            "TAG-2026-00000001",
            TextileType.SHIRT,
            "Cotton",
            List.of(),
            1480);

    CleaningOrderItem suit =
        new CleaningOrderItem(
            UUID.randomUUID(), 2, "TAG-2026-00000002", TextileType.SUIT, "Wool", List.of(), 1990);

    int total = pricingService.calculateOrderTotalMinorUnits(List.of(shirt, suit));

    assertEquals(3470, total);
  }
}
