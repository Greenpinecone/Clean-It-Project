package com.mk.backend.shared.error;

import jakarta.validation.ConstraintViolationException;
import java.net.URI;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(NoResourceFoundException.class)
  ProblemDetail handleNoResourceFound(NoResourceFoundException exception) {
    ProblemDetail problemDetail =
        ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND, "The requested resource was not found.");
    problemDetail.setTitle("Not found");
    problemDetail.setType(URI.create("about:blank"));
    return problemDetail;
  }

  @ExceptionHandler(NotFoundException.class)
  ProblemDetail handleNotFoundException(NotFoundException exception) {
    ProblemDetail problemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    problemDetail.setTitle("Resource not found");
    problemDetail.setType(URI.create("urn:problem-type:not-found"));
    return problemDetail;
  }

  @ExceptionHandler(ConflictException.class)
  ProblemDetail handleConflictException(ConflictException exception) {
    ProblemDetail problemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
    problemDetail.setTitle("Conflict");
    problemDetail.setType(URI.create("urn:problem-type:conflict"));
    return problemDetail;
  }

  @ExceptionHandler(BindException.class)
  ProblemDetail handleBindException(BindException exception) {
    List<FieldValidationError> errors =
        exception.getBindingResult().getFieldErrors().stream()
            .map(this::toFieldValidationError)
            .toList();

    return validationProblem(errors);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  ProblemDetail handleConstraintViolationException(ConstraintViolationException exception) {
    List<FieldValidationError> errors =
        exception.getConstraintViolations().stream()
            .map(
                violation ->
                    new FieldValidationError(
                        violation.getPropertyPath().toString(), violation.getMessage()))
            .toList();

    return validationProblem(errors);
  }

  @ExceptionHandler(ValidationException.class)
  ProblemDetail handleValidationException(ValidationException exception) {
    return validationProblem(exception.errors());
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  ProblemDetail handleMethodArgumentTypeMismatchException(
      MethodArgumentTypeMismatchException exception) {

    String field = exception.getName();
    String message = typeMismatchMessage(field, exception.getRequiredType());

    return validationProblem(List.of(FieldValidationError.of(field, message)));
  }

  @ExceptionHandler(Exception.class)
  ProblemDetail handleUnexpectedException(Exception exception) {
    log.error("Unhandled backend exception", exception);

    ProblemDetail problemDetail =
        ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
    problemDetail.setTitle("Internal server error");
    problemDetail.setType(URI.create("urn:problem-type:internal-server-error"));
    return problemDetail;
  }

  private ProblemDetail validationProblem(List<FieldValidationError> errors) {
    ProblemDetail problemDetail =
        ProblemDetail.forStatusAndDetail(
            HttpStatus.UNPROCESSABLE_CONTENT, "One or more fields are invalid.");
    problemDetail.setTitle("Validation failed");
    problemDetail.setType(URI.create("urn:problem-type:validation-error"));
    problemDetail.setProperty("errors", errors);
    return problemDetail;
  }

  private FieldValidationError toFieldValidationError(FieldError fieldError) {
    if (fieldError.isBindingFailure()) {
      return new FieldValidationError(fieldError.getField(), bindingFailureMessage(fieldError));
    }

    String message =
        fieldError.getDefaultMessage() == null ? "Invalid value." : fieldError.getDefaultMessage();

    return new FieldValidationError(fieldError.getField(), message);
  }

  private String bindingFailureMessage(FieldError fieldError) {
    if ("status".equals(fieldError.getField())) {
      return "Selected status is invalid.";
    }

    if ("targetStatus".equals(fieldError.getField())) {
      return "Selected target status is invalid.";
    }

    return "Invalid value.";
  }

  private String typeMismatchMessage(String field, Class<?> requiredType) {
    if ("status".equals(field)) {
      return "Selected status is invalid.";
    }

    if ("targetStatus".equals(field)) {
      return "Selected target status is invalid.";
    }

    if (requiredType != null && requiredType.isEnum()) {
      return "Selected value is invalid.";
    }

    return "Invalid value.";
  }
}
