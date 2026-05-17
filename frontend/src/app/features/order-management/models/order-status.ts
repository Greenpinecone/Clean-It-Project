export enum OrderStatus {
  Received = 'RECEIVED',
  InCleaning = 'IN_CLEANING',
  ReadyForPickup = 'READY_FOR_PICKUP',
  Returned = 'RETURNED',
  Cancelled = 'CANCELLED',
}

export const ORDER_STATUSES: readonly OrderStatus[] = Object.values(OrderStatus);
