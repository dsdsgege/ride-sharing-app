import { ApplicationConfig, provideBrowserGlobalErrorListeners, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import {
  INCLUDE_BEARER_TOKEN_INTERCEPTOR_CONFIG,
  includeBearerTokenInterceptor,
  provideKeycloak
} from 'keycloak-angular';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import {providePrimeNG} from 'primeng/config';
import Material from '@primeuix/themes/material';
import {provideAnimations} from '@angular/platform-browser/animations';

export const appConfig: ApplicationConfig = {
  providers: [
    providePrimeNG({
      theme: {
        preset: Material
      }
    }),
    provideAnimations(),
    provideKeycloak({
      config: {
        url: "http://192.168.0.37:8090",
        realm: "ride-sharing-app",
        clientId: "ride-share-client-id"
      },
      initOptions: {
        onLoad: 'check-sso',
        checkLoginIframe: false
      }
    }),
    provideBrowserGlobalErrorListeners(),
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(withInterceptors([includeBearerTokenInterceptor])),
    {
      provide: INCLUDE_BEARER_TOKEN_INTERCEPTOR_CONFIG,
      useValue: [
        {
          urlPattern: /^http:\/\/localhost:*/,
          httpMethods: ['GET', 'POST', 'PUT', 'DELETE']
        },
      ],
    }
  ]
};
