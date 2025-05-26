import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const adminGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isLoggedIn() && authService.isAdmin()) {
    return true;
  }

  // Redirect to login if not logged in, or to home if logged in but not admin
  const redirectTo = authService.isLoggedIn() ? '/' : '/login';
  return router.parseUrl(redirectTo);
};
