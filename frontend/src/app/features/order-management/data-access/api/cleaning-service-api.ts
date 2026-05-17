import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { API_BASE_URL } from '@core/config/app-config-tokens';

import { CleaningServiceResponse } from '../dto/cleaning-service-response';

@Injectable({
  providedIn: 'root',
})
export class CleaningServiceApi {
  private readonly http = inject(HttpClient);
  private readonly apiBaseUrl = inject(API_BASE_URL);

  getCleaningServices(): Observable<CleaningServiceResponse[]> {
    return this.http.get<CleaningServiceResponse[]>(`${this.apiBaseUrl}/cleaning-services`);
  }
}
