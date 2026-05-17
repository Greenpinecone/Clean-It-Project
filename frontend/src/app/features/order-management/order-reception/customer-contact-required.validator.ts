import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export const customerContactRequiredValidator: ValidatorFn = (
  control: AbstractControl,
): ValidationErrors | null => {
  const email = control.get('email')?.value;
  const phoneNumber = control.get('phoneNumber')?.value;

  const hasEmail = email.trim().length > 0;
  const hasPhoneNumber = phoneNumber.trim().length > 0;

  return hasEmail || hasPhoneNumber ? null : { customerContactRequired: true };
};
