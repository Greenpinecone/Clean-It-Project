package com.mk.backend.ordermanagement.application;

import com.mk.backend.ordermanagement.model.SelectedCleaningService;
import com.mk.backend.ordermanagement.model.entity.CleaningOrderItem;
import java.util.List;

public interface PricingService {

  int calculateItemPriceMinorUnits(List<SelectedCleaningService> services);

  int calculateOrderTotalMinorUnits(List<CleaningOrderItem> items);
}
