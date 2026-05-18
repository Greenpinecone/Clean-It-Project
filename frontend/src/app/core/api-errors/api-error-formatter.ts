import { Injectable } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';

import { getApiProblem } from './api-problem';

@Injectable({ providedIn: 'root' })
export class ApiErrorFormatter {
  format(error: unknown): string {
    if (!(error instanceof HttpErrorResponse)) {
      return 'An unknown error occurred.';
    }

    if (error.status === 0) {
      return 'The server is currently unavailable.';
    }

    if (error.status >= 500) {
      return 'A server error occurred.';
    }

    const problem = getApiProblem(error);
    const backendMessage = problem?.detail ?? problem?.message ?? problem?.title;

    if (backendMessage?.trim()) {
      return backendMessage;
    }

    return this.messageFromStatus(error.status);
  }

  private messageFromStatus(status: number): string {
    switch (status) {
      case 400:
        return 'The request is invalid.';
      case 401:
        return 'You are not signed in.';
      case 403:
        return 'You do not have permission to perform this action.';
      case 404:
        return 'The requested record was not found.';
      case 409:
        return 'The record could not be saved because of a conflict.';
      default:
        return 'An unknown error occurred.';
    }
  }
}
