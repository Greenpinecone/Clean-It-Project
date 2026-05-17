import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { API_BASE_URL } from '@core/config/app-config-tokens';

import { OrderSummaryResponse } from '../dto/order-summary-response';
import { CreateOrderRequest } from '../dto/create-order-request';

@Injectable({
  providedIn: 'root',
})
export class OrderApi {
  private readonly http = inject(HttpClient);
  private readonly apiBaseUrl = inject(API_BASE_URL);

  createOrder(request: CreateOrderRequest): Observable<OrderSummaryResponse> {
    return this.http.post<OrderSummaryResponse>(`${this.apiBaseUrl}/orders`, request);
  }
}
