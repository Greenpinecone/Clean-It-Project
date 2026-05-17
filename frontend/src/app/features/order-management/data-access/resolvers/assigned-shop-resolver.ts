import { inject } from '@angular/core';
import { ResolveFn } from '@angular/router';
import { ShopApi } from '../api/shop-api';
import { AssignedShopResponse } from '../dto/assigned-shop-response';

export const assignedShopResolver: ResolveFn<AssignedShopResponse> = () => {
  const shopApi = inject(ShopApi);
  return shopApi.getAssignedShop();
};
