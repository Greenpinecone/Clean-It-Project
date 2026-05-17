import { HttpErrorResponse } from '@angular/common/http';

export const VALIDATION_ERROR_STATUS = 422;

export interface ApiValidationError {
  field?: string;
  message: string;
}

export interface ApiProblem {
  type?: string;
  title?: string;
  status?: number;
  detail?: string;
  message?: string;
  errors?: ApiValidationError[];
}

export function isValidationErrorResponse(error: unknown): boolean {
  return error instanceof HttpErrorResponse && error.status === VALIDATION_ERROR_STATUS;
}

export function getApiProblem(error: unknown): ApiProblem | null {
  if (!(error instanceof HttpErrorResponse)) {
    return null;
  }

  if (!isRecord(error.error)) {
    return null;
  }

  return error.error as ApiProblem;
}

export function getApiValidationErrors(error: unknown): ApiValidationError[] {
  if (!isValidationErrorResponse(error)) {
    return [];
  }

  const problem = getApiProblem(error);

  if (!Array.isArray(problem?.errors)) {
    return [];
  }

  return problem.errors.filter(isApiValidationError);
}

function isApiValidationError(value: unknown): value is ApiValidationError {
  if (!isRecord(value)) {
    return false;
  }

  const field = value['field'];
  const message = value['message'];

  return (
    typeof message === 'string' &&
    message.trim().length > 0 &&
    (field === undefined || typeof field === 'string')
  );
}

function isRecord(value: unknown): value is Record<string, unknown> {
  return typeof value === 'object' && value !== null;
}
