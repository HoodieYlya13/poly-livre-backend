package com.poly.livre.backend.exceptions.errors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public enum SystemErrorCode implements ErrorCode {

    VALIDATION_FAILED("SYST_001", "Validation failed for at least one field"),
    TECHNICAL_ERROR("SYST_002", "An unexpected exception occurred");

    private final String code;
    private final String description;

}