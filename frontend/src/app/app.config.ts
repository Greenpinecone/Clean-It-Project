import {
  ApplicationConfig,
  provideBrowserGlobalErrorListeners,
  DEFAULT_CURRENCY_CODE,
  LOCALE_ID,
  inject,
  ErrorHandler,
} from '@angular/core';
import {
  provideRouter,
  RedirectCommand,
  Router,
  withNavigationErrorHandler,
} from '@angular/router';
import { HttpErrorResponse, provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideToastr } from 'ngx-toastr';
import { apiErrorToastInterceptor } from './core/http/interceptors/api-error-toast-interceptor';

import { routes } from './app.routes';
import { ToastNotifier } from '@core/notifications/toast-notifier';
import { GlobalErrorHandler } from '@core/api-errors/global-error-handler';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    {
      provide: ErrorHandler,
      useClass: GlobalErrorHandler,
    },
    provideRouter(
      routes,
      withNavigationErrorHandler((navigationError) => {
        const router = inject(Router);
        const toastNotifier = inject(ToastNotifier);

        if (!(navigationError.error instanceof HttpErrorResponse)) {
          toastNotifier.error('The requested page could not be opened.');
        }

        return new RedirectCommand(router.parseUrl('/'));
      }),
    ),
    provideHttpClient(withInterceptors([apiErrorToastInterceptor])),
    provideToastr({
      enableHtml: true,
      timeOut: 3000,
      extendedTimeOut: 2000,
      progressBar: true,
      positionClass: 'toast-top-right',
      preventDuplicates: true,
      closeButton: true,
    }),
    { provide: LOCALE_ID, useValue: 'de-AT' },
    { provide: DEFAULT_CURRENCY_CODE, useValue: 'EUR' },
  ],
};
