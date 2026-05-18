import { inject } from '@angular/core';
import { ResolveFn } from '@angular/router';

import { CleaningServiceApi } from '../api/cleaning-service-api';
import { CleaningServiceResponse } from '../dto/cleaning-service-response';

export const cleaningServicesResolver: ResolveFn<CleaningServiceResponse[]> = () => {
  const cleaningServiceApi = inject(CleaningServiceApi);

  return cleaningServiceApi.getCleaningServices();
};
