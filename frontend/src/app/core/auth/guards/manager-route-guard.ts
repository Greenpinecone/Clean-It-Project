import { inject } from '@angular/core';
import { CanActivateChildFn, Router } from '@angular/router';
import { DemoUser } from '../demo-user';

export const managerRouteGuard: CanActivateChildFn = () => {
  const demoUser = inject(DemoUser);
  const router = inject(Router);

  if (demoUser.isManager()) {
    return true;
  }

  return router.createUrlTree(['/orders/create']);
};
