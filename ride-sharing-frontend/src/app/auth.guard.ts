import { AuthGuardData, createAuthGuard } from 'keycloak-angular';
import {ActivatedRouteSnapshot, CanActivateFn, RouterStateSnapshot} from '@angular/router';

const isAccessAllowed = async (
  route: ActivatedRouteSnapshot,
  __: RouterStateSnapshot,
  authData: AuthGuardData
): Promise<boolean> => {
  const { authenticated, grantedRoles, keycloak } = authData;

  try {
    if(!authenticated) {
      await keycloak.login()
      return keycloak.authenticated === true;
    } else {
      return true;
    }
  } catch (error) {
    alert("Error: " + error);
    return false;
  }
};

export const canActivateAuthRole = createAuthGuard<CanActivateFn>(isAccessAllowed);
