import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors, withFetch } from '@angular/common/http';
import { provideAnimations } from '@angular/platform-browser/animations';
import { provideToastr } from 'ngx-toastr';

import { authInterceptor } from './interceptors/auth.interceptor';
import { routes } from './app.routes'; // Importez les routes directement

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes), 
    provideHttpClient(
      withInterceptors([authInterceptor]),
      withFetch() 
    ),
    provideAnimations(), 
    provideToastr({ 
      timeOut: 5000, 
      positionClass: 'toast-top-right', 
      preventDuplicates: true,
    }),
  ]
};