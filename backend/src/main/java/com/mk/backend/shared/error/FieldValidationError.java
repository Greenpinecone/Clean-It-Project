package com.mk.backend.shared.error;

public record FieldValidationError(String field, String message) {

  public static FieldValidationError of(String field, String message) {
    return new FieldValidationError(field, message);
  }
}
