import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { KeyCloakService } from '../services/keycloak/keycloak.service';

export const authGuard: CanActivateFn = (route, state) => {
  const keyCloakService = inject(KeyCloakService);
  const router = inject(Router)
  
  if(keyCloakService.keycloak.isTokenExpired()) {
    router.navigate(['/login'])
  }
  return true;
};
