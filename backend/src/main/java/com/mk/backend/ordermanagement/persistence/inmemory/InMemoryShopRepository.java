package com.mk.backend.ordermanagement.persistence.inmemory;

import com.mk.backend.ordermanagement.model.entity.Shop;
import com.mk.backend.ordermanagement.persistence.ShopRepository;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
class InMemoryShopRepository implements ShopRepository {

  private final Map<String, Shop> shopsByCode = new ConcurrentHashMap<>();

  InMemoryShopRepository() {
    Shop viennaShop =
        new Shop(
            UUID.fromString("a4d70038-dd60-4d88-91df-add09ecd011f"),
            "VIE-01",
            "Clean It! Vienna, 1010, Schottentor 3");

    shopsByCode.put(viennaShop.shopCode(), viennaShop);
  }

  @Override
  public Optional<Shop> findByShopCode(String shopCode) {
    return Optional.ofNullable(shopsByCode.get(shopCode));
  }

  @Override
  public boolean existsByShopCode(String shopCode) {
    return shopsByCode.containsKey(shopCode);
  }
}
