import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { API_BASE_URL } from '@core/config/app-config-tokens';

import { AssignedShopResponse } from '../dto/assigned-shop-response';

@Injectable({
  providedIn: 'root',
})
export class ShopApi {
  private readonly http = inject(HttpClient);
  private readonly apiBaseUrl = inject(API_BASE_URL);

  getAssignedShop(): Observable<AssignedShopResponse> {
    return this.http.get<AssignedShopResponse>(`${this.apiBaseUrl}/shops/assigned`);
  }
}
