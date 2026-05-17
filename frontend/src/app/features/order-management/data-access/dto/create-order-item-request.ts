import { CleaningServiceCode } from '@features/order-management/models/cleaning-service-codes';
import { TextileType } from '../../models/textile-type';

export interface CreateOrderItemRequest {
  textileType: TextileType;
  quantity: number;
  material?: string;
  serviceCodes: CleaningServiceCode[];
}
