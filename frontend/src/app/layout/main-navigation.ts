import { NavItem } from './nav-item';
import { DemoRole } from '@core/auth/demo-user-model';

export const MAIN_NAVIGATION: readonly NavItem[] = [
  {
    label: 'Dashboard',
    route: '/',
  },
  {
    label: 'Customers',
    route: '/customers',
  },
  {
    label: 'New Order',
    route: '/orders/create',
  },
];

export const MANAGER_NAVIGATION: readonly NavItem[] = [
  {
    label: 'Dashboard',
    route: '/manager/dashboard',
    roles: [DemoRole.Manager],
  },
  {
    label: 'Orders',
    route: '/manager/orders',
    roles: [DemoRole.Manager],
  },
];
