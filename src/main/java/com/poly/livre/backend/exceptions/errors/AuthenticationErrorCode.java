package com.poly.livre.backend.exceptions.errors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public enum AuthenticationErrorCode implements ErrorCode {

    FAILED("AUTH.001", "Authentication failed"),
    UNAUTHORIZED("AUTH.002", "Unauthorized (NOT USED YET)"),
    JWT_INVALID("AUTH.003", "Invalid JWT token"),
    JWT_EXPIRED("AUTH.004", "JWT token expired");

    private final String code;
    private final String description;

}