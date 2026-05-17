import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { API_BASE_URL } from '@core/config/app-config-tokens';
import { PageResponse } from '@shared/models/page-response';

import { ChangeOrderStatusRequest } from '../dto/change-order-status-request';
import { OrderSearchParams } from '../../models/order-search-params';
import { OrderSummaryResponse } from '../dto/order-summary-response';
import { CreateOrderRequest } from '../dto/create-order-request';

@Injectable({
  providedIn: 'root',
})
export class OrderApi {
  private readonly http = inject(HttpClient);
  private readonly apiBaseUrl = inject(API_BASE_URL);

  searchOrders(params: OrderSearchParams): Observable<PageResponse<OrderSummaryResponse>> {
    return this.http.get<PageResponse<OrderSummaryResponse>>(`${this.apiBaseUrl}/orders`, {
      params: this.toHttpParams(params),
    });
  }

  createOrder(request: CreateOrderRequest): Observable<OrderSummaryResponse> {
    return this.http.post<OrderSummaryResponse>(`${this.apiBaseUrl}/orders`, request);
  }

  changeOrderStatus(
    orderId: string,
    request: ChangeOrderStatusRequest,
  ): Observable<OrderSummaryResponse> {
    return this.http.patch<OrderSummaryResponse>(
      `${this.apiBaseUrl}/orders/${orderId}/status`,
      request,
    );
  }

  private toHttpParams(params: OrderSearchParams): HttpParams {
    let httpParams = new HttpParams()
      .set('shopCode', params.shopCode)
      .set('page', params.page)
      .set('size', params.size);

    const query = params.query?.trim();

    if (query) {
      httpParams = httpParams.set('query', query);
    }

    if (params.status) {
      httpParams = httpParams.set('status', params.status);
    }

    return httpParams;
  }
}
