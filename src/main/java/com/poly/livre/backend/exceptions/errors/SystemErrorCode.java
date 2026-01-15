package com.poly.livre.backend.exceptions.errors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public enum SystemErrorCode implements ErrorCode {

    TECHNICAL_ERROR("SYST.001", "An unexpected exception occurred"),
    VALIDATION_FAILED("SYST.002", "Validation failed for at least one field");

    private final String code;
    private final String description;

}