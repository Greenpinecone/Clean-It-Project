package com.mk.backend.ordermanagement.mapper;

import com.mk.backend.ordermanagement.model.entity.Shop;
import com.mk.backend.ordermanagement.web.dto.response.AssignedShopResponse;
import org.springframework.stereotype.Component;

@Component
public class ShopMapper {

  public AssignedShopResponse toAssignedShopResponse(Shop shop) {
    return new AssignedShopResponse(shop.shopCode(), shop.name());
  }
}
