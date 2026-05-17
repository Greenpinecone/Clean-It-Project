package com.mk.backend.ordermanagement.persistence;

import com.mk.backend.ordermanagement.model.entity.CleaningOrder;
import com.mk.backend.ordermanagement.model.type.OrderStatus;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {

  Optional<CleaningOrder> findById(UUID id);

  OrderSearchResult searchOrders(
      String shopCode, String query, OrderStatus status, int page, int size);

  CleaningOrder save(CleaningOrder order);
}
