import { AbstractControl, FormArray, FormGroup } from '@angular/forms';

import { ApiValidationError } from '@core/api-errors/api-problem';

export function applyServerValidationErrors(
  form: FormGroup,
  errors: readonly ApiValidationError[],
  handleUnmappedError?: (message: string) => void,
): void {
  for (const error of errors) {
    const fieldPath = error.field ? normalizeFieldPath(error.field) : null;
    const control = fieldPath ? form.get(fieldPath) : null;

    if (!control) {
      handleUnmappedError?.(error.message);
      continue;
    }

    control.setErrors({
      ...control.errors,
      server: error.message,
    });

    control.markAsTouched();
  }
}

export function clearServerValidationErrors(control: AbstractControl): void {
  if (control.errors?.['server']) {
    const { server, ...remainingErrors } = control.errors;

    console.log(server);
    control.setErrors(Object.keys(remainingErrors).length > 0 ? remainingErrors : null);
  }

  if (control instanceof FormGroup || control instanceof FormArray) {
    Object.values(control.controls).forEach((childControl) =>
      clearServerValidationErrors(childControl),
    );
  }
}

function normalizeFieldPath(field: string): string {
  return field.replace(/\[(\d+)]/g, '.$1');
}
