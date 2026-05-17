package com.mk.backend.ordermanagement.application.impl;

import com.mk.backend.ordermanagement.application.PricingService;
import com.mk.backend.ordermanagement.model.SelectedCleaningService;
import com.mk.backend.ordermanagement.model.entity.CleaningOrderItem;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
class SimplePricingService implements PricingService {

  @Override
  public int calculateItemPriceMinorUnits(List<SelectedCleaningService> services) {
    return services.stream().mapToInt(SelectedCleaningService::priceMinorUnits).sum();
  }

  @Override
  public int calculateOrderTotalMinorUnits(List<CleaningOrderItem> items) {
    return items.stream().mapToInt(CleaningOrderItem::itemPriceMinorUnits).sum();
  }
}
