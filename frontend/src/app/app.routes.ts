import { Routes } from '@angular/router';
import { managerRouteGuard } from './core/auth/guards/manager-route-guard';

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    loadComponent: () =>
      import('./shared/ui/page-placeholder/page-placeholder').then((m) => m.PagePlaceholder),
    title: 'Clean It! AG',
    data: {
      heading: 'Clean It! AG',
      badge: 'Application shell',
      description:
        'Base Angular application shell for the Clean It! AG shop workflow prototype.',
    },
  },
  {
    path: 'customers',
    loadComponent: () =>
      import('./shared/ui/page-placeholder/page-placeholder').then((m) => m.PagePlaceholder),
    title: 'Customers',
    data: {
      heading: 'Customers',
      badge: 'Prepared route',
      description: 'Customer management is prepared in the application shell.',
    },
  },
  {
    path: 'orders/create',
    loadComponent: () =>
      import('./shared/ui/page-placeholder/page-placeholder').then((m) => m.PagePlaceholder),
    title: 'New order',
    data: {
      heading: 'New order',
      badge: 'Prepared route',
      description: 'The order reception workflow will be added in the next implementation step.',
    },
  },
  {
    path: 'manager',
    canActivateChild: [managerRouteGuard],
    loadChildren: () =>
      import('./features/order-management/order-management.routes').then(
        (m) => m.ORDER_MANAGEMENT_ROUTES,
      ),
  },
  {
    path: '**',
    loadComponent: () => import('./shared/ui/not-found/not-found').then((m) => m.NotFound),
    title: 'Page not found',
  },
];
