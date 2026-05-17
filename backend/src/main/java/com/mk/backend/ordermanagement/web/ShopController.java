package com.mk.backend.ordermanagement.web;

import com.mk.backend.ordermanagement.application.ShopService;
import com.mk.backend.ordermanagement.web.dto.response.AssignedShopResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/shops")
@Tag(name = "Shops")
public class ShopController {

  private final ShopService shopService;

  ShopController(ShopService shopService) {
    this.shopService = shopService;
  }

  @GetMapping("/assigned")
  @Operation(summary = "Get the assigned shop for the current demo user")
  public ResponseEntity<AssignedShopResponse> getAssignedShop() {
    return ResponseEntity.ok(shopService.getAssignedShop());
  }
}
