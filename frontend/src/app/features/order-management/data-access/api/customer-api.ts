import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { API_BASE_URL } from '@core/config/app-config-tokens';

import { CustomerSearchParams } from '../../models/customer-search-params';
import { CustomerSearchResponse } from '../dto/customer-search-response';

@Injectable({
  providedIn: 'root',
})
export class CustomerApi {
  private readonly http = inject(HttpClient);
  private readonly apiBaseUrl = inject(API_BASE_URL);

  searchCustomers(params: CustomerSearchParams): Observable<CustomerSearchResponse[]> {
    return this.http.get<CustomerSearchResponse[]>(`${this.apiBaseUrl}/customers`, {
      params: this.toHttpParams(params),
    });
  }

  private toHttpParams(params: CustomerSearchParams): HttpParams {
    let httpParams = new HttpParams();

    if (params.displayName) {
      httpParams = httpParams.set('displayName', params.displayName);
    }

    if (params.email) {
      httpParams = httpParams.set('email', params.email);
    }

    if (params.phoneNumber) {
      httpParams = httpParams.set('phoneNumber', params.phoneNumber);
    }

    return httpParams;
  }
}
