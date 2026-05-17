package com.mk.backend.ordermanagement.persistence;

import com.mk.backend.ordermanagement.model.entity.Shop;
import java.util.Optional;

public interface ShopRepository {

  Optional<Shop> findByShopCode(String shopCode);

  boolean existsByShopCode(String shopCode);
}
