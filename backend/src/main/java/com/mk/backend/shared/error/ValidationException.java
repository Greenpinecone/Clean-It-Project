package com.mk.backend.shared.error;

import java.util.List;

public class ValidationException extends RuntimeException {

  private final List<FieldValidationError> errors;

  public ValidationException(List<FieldValidationError> errors) {
    super("One or more fields are invalid.");
    this.errors = List.copyOf(errors);
  }

  public List<FieldValidationError> errors() {
    return errors;
  }
}
