package com.poly.livre.backend.configurations;

import com.poly.livre.backend.exceptions.*;
import com.poly.livre.backend.exceptions.errors.AuthenticationErrorCode;
import com.poly.livre.backend.exceptions.errors.SystemErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.nio.file.AccessDeniedException;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@ControllerAdvice
public class ExceptionHandlerControllerAdvice {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorMessage> handleNotFound(NotFoundException ex) {
        log.error("Not found exception: {}", ex.getErrorCode().printableError());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorMessage.fromErrorCodeException(ex));
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorMessage> handleBadRequest(BadRequestException ex) {
        log.error("Bad request exception: {}", ex.getErrorCode().printableError());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorMessage.fromErrorCodeException(ex));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorMessage> handleDtoValidationFailed(MethodArgumentNotValidException ex) {
        log.error("DTO Validation failed: {}", String.join(", ", ex.getBindingResult().getFieldErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList()));
        Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> Optional.ofNullable(fieldError.getDefaultMessage()).orElse("Undetermined reason"))
                );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ValidationErrorMessage(fieldErrors));
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ErrorMessage> handleForbidden(ForbiddenException ex) {
        log.error("Forbidden exception: {}", ex.getErrorCode().printableError());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ErrorMessage.fromErrorCodeException(ex));
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public @ResponseBody ErrorMessage handleAccessDenied(AccessDeniedException ex) {
        log.error("Access denied exception: {}", ex.getLocalizedMessage());

        return ErrorMessage.fromErrorCode(AuthenticationErrorCode.FAILED);
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ErrorMessage> handleUnauthorized(UnauthorizedException ex) {
        log.error("Unauthorized exception: {}", ex.getErrorCode().printableError());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorMessage.fromErrorCodeException(ex));
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ErrorMessage> handleConflict(ConflictException ex) {
        log.error("Conflict exception: {}", ex.getErrorCode().printableError());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorMessage.fromErrorCodeException(ex));
    }

    @ExceptionHandler(InternalServerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorMessage> handleInternalServer(InternalServerException ex) {
        log.error("Internal server exception: {}", ex.getErrorCode().printableError());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorMessage.fromErrorCodeException(ex));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public @ResponseBody ErrorMessage handleRuntimeException(Exception ex) {
        log.error("Unhandled exception", ex);

        return ErrorMessage.fromErrorCode(SystemErrorCode.TECHNICAL_ERROR);
    }
    
}