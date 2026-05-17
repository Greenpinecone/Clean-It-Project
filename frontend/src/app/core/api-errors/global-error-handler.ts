import { ErrorHandler, Injectable, inject } from '@angular/core';
import { Router } from '@angular/router';
import { ToastNotifier } from '@core/notifications/toast-notifier';

@Injectable()
export class GlobalErrorHandler implements ErrorHandler {
  private readonly router = inject(Router);
  private readonly toastNotifier = inject(ToastNotifier);

  handleError(error: unknown): void {
    console.error('Unhandled application error', {
      url: this.router.url,
      error,
    });

    this.toastNotifier.error('An unexpected application error occurred.');
  }
}
