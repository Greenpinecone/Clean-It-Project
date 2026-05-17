import { CleaningServiceCode } from '@features/order-management/models/cleaning-service-codes';

export interface CleaningServiceResponse {
  code: CleaningServiceCode;
  displayName: string;
  priceMinorUnits: number;
}
