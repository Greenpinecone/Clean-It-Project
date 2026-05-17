import { CreateOrderCustomerRequest } from './create-order-customer-request';
import { CreateOrderItemRequest } from './create-order-item-request';

export interface CreateOrderRequest {
  shopCode: string;
  expectedPickupDate: string;
  customer: CreateOrderCustomerRequest;
  items: CreateOrderItemRequest[];
}
