import { Injectable } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';

import { getApiProblem } from './api-problem';

@Injectable({ providedIn: 'root' })
export class ApiErrorFormatter {
  format(error: unknown): string {
    if (!(error instanceof HttpErrorResponse)) {
      return 'Ein unbekannter Fehler ist aufgetreten.';
    }

    if (error.status === 0) {
      return 'Der Server ist derzeit nicht erreichbar.';
    }

    if (error.status >= 500) {
      return 'Auf dem Server ist ein Fehler aufgetreten.';
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
        return 'Die Anfrage ist ungültig.';
      case 401:
        return 'Sie sind nicht angemeldet.';
      case 403:
        return 'Sie haben keine Berechtigung für diese Aktion.';
      case 404:
        return 'Der angeforderte Eintrag wurde nicht gefunden.';
      case 409:
        return 'Der Eintrag konnte wegen eines Konflikts nicht gespeichert werden.';
      default:
        return 'Ein unbekannter Fehler ist aufgetreten.';
    }
  }
}
