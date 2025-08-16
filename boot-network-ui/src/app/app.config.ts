import { ApplicationConfig, inject, provideAppInitializer, provideBrowserGlobalErrorListeners, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { provideHttpClient } from '@angular/common/http';
import { KeyCloakService } from './shared/services/keycloak/keycloak.service';

export const appConfig: ApplicationConfig = {
  providers: [
     provideAppInitializer(() => {
      const keycloakService = inject(KeyCloakService);
      return keycloakService.init();
    }),
    provideBrowserGlobalErrorListeners(),
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(),
  ]
};
