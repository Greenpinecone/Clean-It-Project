import { OrderStatus } from './order-status';

export interface OrderSearchParams {
  shopCode: string;
  query?: string;
  status?: OrderStatus;
  page: number;
  size: number;
}
