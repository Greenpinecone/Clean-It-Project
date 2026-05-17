package com.mk.backend.ordermanagement.application.impl;

import com.mk.backend.ordermanagement.application.ShopService;
import com.mk.backend.ordermanagement.mapper.ShopMapper;
import com.mk.backend.ordermanagement.model.entity.Shop;
import com.mk.backend.ordermanagement.persistence.ShopRepository;
import com.mk.backend.ordermanagement.web.dto.response.AssignedShopResponse;
import com.mk.backend.shared.error.NotFoundException;
import org.springframework.stereotype.Service;

@Service
class DefaultShopService implements ShopService {

  private static final String DEMO_ASSIGNED_SHOP_CODE = "VIE-01";

  private final ShopRepository shopRepository;
  private final ShopMapper shopMapper;

  DefaultShopService(ShopRepository shopRepository, ShopMapper shopMapper) {
    this.shopRepository = shopRepository;
    this.shopMapper = shopMapper;
  }

  @Override
  public AssignedShopResponse getAssignedShop() {
    Shop shop =
        shopRepository
            .findByShopCode(DEMO_ASSIGNED_SHOP_CODE)
            .orElseThrow(
                () ->
                    new NotFoundException(
                        "Assigned demo shop with code %s was not found."
                            .formatted(DEMO_ASSIGNED_SHOP_CODE)));

    return shopMapper.toAssignedShopResponse(shop);
  }
}
