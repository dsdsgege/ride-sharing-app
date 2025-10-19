import Keycloak from 'keycloak-js';
import {Injectable} from '@angular/core';

@Injectable({
  providedIn: 'root'
})

export class KeycloakInstanceService {
  private keycloakInstance!: Keycloak;

  setInstance(keycloakInstance: Keycloak) {
    this.keycloakInstance = keycloakInstance;
  }

  getInstance(): Keycloak {
    return this.keycloakInstance;
  }
}
