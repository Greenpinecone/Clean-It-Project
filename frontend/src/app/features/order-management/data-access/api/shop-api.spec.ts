import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';

import { API_BASE_URL } from '@core/config/app-config-tokens';

import { AssignedShopResponse } from '../dto/assigned-shop-response';
import { ShopApi } from './shop-api';

describe('ShopApi', () => {
  let api: ShopApi;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });

    api = TestBed.inject(ShopApi);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('loads the assigned shop', () => {
    const apiBaseUrl = TestBed.inject(API_BASE_URL);

    const response: AssignedShopResponse = {
      shopCode: 'VIE-01',
      name: 'Clean It! Wien Zentrum',
    };

    let actualResponse: AssignedShopResponse | undefined;

    api.getAssignedShop().subscribe((shop) => {
      actualResponse = shop;
    });

    const request = httpTestingController.expectOne(`${apiBaseUrl}/shops/assigned`);

    expect(request.request.method).toBe('GET');

    request.flush(response);

    expect(actualResponse).toEqual(response);
  });
});
