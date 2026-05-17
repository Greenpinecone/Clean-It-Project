import { NavItem } from './nav-item';

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
