import { ApplicationConfig, provideBrowserGlobalErrorListeners, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import {KeycloakBearerInterceptor, KeycloakService, provideKeycloak} from 'keycloak-angular';
import {HTTP_INTERCEPTORS} from '@angular/common/http';

export const appConfig: ApplicationConfig = {
  providers: [
    provideKeycloak({
      config: {
        url: "http://localhost:8090",
        realm: "ride-sharing-app",
        clientId: "ride-share-client-id"
      },
      initOptions: {
        onLoad: 'login-required',
        checkLoginIframe: false
      }
    }),
    provideBrowserGlobalErrorListeners(),
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    KeycloakService,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: KeycloakBearerInterceptor,
      multi: true
    }
  ]
};
