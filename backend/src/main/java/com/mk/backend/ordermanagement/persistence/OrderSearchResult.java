package com.mk.backend.ordermanagement.persistence;

import com.mk.backend.ordermanagement.model.entity.CleaningOrder;
import java.util.List;

public record OrderSearchResult(List<CleaningOrder> items, int totalItems) {

  public OrderSearchResult {
    items = List.copyOf(items);
  }
}
