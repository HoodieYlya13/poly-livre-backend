package com.poly.livre.backend.exceptions;

import com.poly.livre.backend.exceptions.errors.SystemErrorCode;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.time.OffsetDateTime;
import java.util.Map;

@Value
@EqualsAndHashCode(callSuper = true)
public class ValidationErrorMessage extends ErrorMessage {

    Map<String, String> fieldErrors;

    public ValidationErrorMessage(Map<String, String> fieldErrors) {
        super(SystemErrorCode.VALIDATION_FAILED.getCode(), SystemErrorCode.VALIDATION_FAILED.getDescription(), OffsetDateTime.now());
        this.fieldErrors = fieldErrors;
    }

}
