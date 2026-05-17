import { OrderStatus } from '../../models/order-status';

export interface ChangeOrderStatusRequest {
  targetStatus: OrderStatus;
}
