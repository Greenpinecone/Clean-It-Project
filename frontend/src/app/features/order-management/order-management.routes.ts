import { Routes } from '@angular/router';

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
      import('@shared/ui/page-placeholder/page-placeholder').then((m) => m.PagePlaceholder),
    title: 'Order overview',
    data: {
      heading: 'Order overview',
      badge: 'Manager area',
      description: 'The manager order overview will be added in the next implementation step.',
    },
  },
];
