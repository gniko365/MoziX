import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { provideHttpClient } from '@angular/common/http'; // Importáld

export const appConfig: ApplicationConfig = {
  providers: [provideRouter(routes), provideHttpClient()] // Add hozzá a provideHttpClient-t
};