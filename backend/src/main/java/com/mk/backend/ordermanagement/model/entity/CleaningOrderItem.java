package com.mk.backend.ordermanagement.model.entity;

import com.mk.backend.ordermanagement.model.SelectedCleaningService;
import com.mk.backend.ordermanagement.model.type.TextileType;
import java.util.List;
import java.util.UUID;

public record CleaningOrderItem(
    UUID id,
    int lineNumber,
    String itemCode,
    TextileType type,
    String material,
    List<SelectedCleaningService> services,
    int itemPriceMinorUnits) {

  public CleaningOrderItem {
    services = List.copyOf(services);
  }
}
