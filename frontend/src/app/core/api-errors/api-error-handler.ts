import { Injectable, inject } from '@angular/core';

import { ApiErrorFormatter } from './api-error-formatter';
import { isValidationErrorResponse } from './api-problem';
import { ToastNotifier } from '../notifications/toast-notifier';

@Injectable({ providedIn: 'root' })
export class ApiErrorHandler {
  private readonly formatter = inject(ApiErrorFormatter);
  private readonly toastNotifier = inject(ToastNotifier);

  handleError(error: unknown): boolean {
    if (isValidationErrorResponse(error)) {
      return false;
    }

    this.toastNotifier.error(this.formatter.format(error));
    return true;
  }
}
