import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';

import { ApiErrorHandler } from '../../api-errors/api-error-handler';
import { isValidationErrorResponse } from '../../api-errors/api-problem';
import { API_BASE_URL } from '../../config/app-config-tokens';

export const apiErrorToastInterceptor: HttpInterceptorFn = (request, next) => {
  const apiBaseUrl = inject(API_BASE_URL);
  const apiErrorHandler = inject(ApiErrorHandler);

  return next(request).pipe(
    catchError((error: unknown) => {
      const shouldHandleGlobally =
        error instanceof HttpErrorResponse &&
        isApiRequest(request.url, apiBaseUrl) &&
        !isValidationErrorResponse(error);

      if (shouldHandleGlobally) {
        apiErrorHandler.handleError(error);
      }

      return throwError(() => error);
    }),
  );
};

function isApiRequest(url: string, apiBaseUrl: string): boolean {
  return url.startsWith(apiBaseUrl);
}
