import { OrderStatus } from '../../models/order-status';

export interface OrderSummaryResponse {
  id: string;
  orderNumber: string;
  customerDisplayName: string;
  shopCode: string;
  status: OrderStatus;
  receivedAt: string;
  expectedPickupDate: string;
  itemCount: number;
  totalPriceMinorUnits: number;
}
