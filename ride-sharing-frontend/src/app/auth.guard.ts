import {inject} from '@angular/core';
import {KeycloakService} from 'keycloak-angular';
import {CanActivateFn, Router} from '@angular/router';

export const authGuard: CanActivateFn = (route, state) => {
  const keycloakService = inject(KeycloakService);
  const router = inject(Router);

  return keycloakService.isLoggedIn()
}
