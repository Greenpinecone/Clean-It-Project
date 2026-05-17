import { Routes } from '@angular/router';
import { assignedShopResolver } from './data-access/resolvers/assigned-shop-resolver';

export const ORDER_MANAGEMENT_ROUTES: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'dashboard',
  },
  {
    path: 'dashboard',
    loadComponent: () =>
      import('@shared/ui/page-placeholder/page-placeholder').then((m) => m.PagePlaceholder),
    title: 'Manager dashboard',
    data: {
      heading: 'Manager dashboard',
      badge: 'Manager area',
      description:
        'This page is reserved for manager-specific shop operations and key workload indicators.',
    },
  },
  {
    path: 'orders',
    loadComponent: () =>
      import('./manager-order-overview/manager-order-overview').then(
        (m) => m.ManagerOrderOverview,
      ),
    title: 'Order overview',
    resolve: {
      assignedShop: assignedShopResolver,
    },
  },
];
