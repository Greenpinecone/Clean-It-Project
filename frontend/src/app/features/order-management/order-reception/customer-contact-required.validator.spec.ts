import { FormControl, FormGroup } from '@angular/forms';
import { describe, expect, it } from 'vitest';

import { customerContactRequiredValidator } from './customer-contact-required.validator';

describe('customerContactRequiredValidator', () => {
  it('requires either an email address or a phone number', () => {
    const form = new FormGroup(
      {
        email: new FormControl('', { nonNullable: true }),
        phoneNumber: new FormControl('', { nonNullable: true }),
      },
      { validators: [customerContactRequiredValidator] },
    );

    expect(form.hasError('customerContactRequired')).toBe(true);

    form.controls.email.setValue('max@example.com');

    expect(form.hasError('customerContactRequired')).toBe(false);

    form.controls.email.setValue('');
    form.controls.phoneNumber.setValue('+43 660 0000001');

    expect(form.hasError('customerContactRequired')).toBe(false);
  });
});
