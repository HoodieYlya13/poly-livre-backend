package com.poly.livre.backend.exceptions.errors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public enum AuthenticationErrorCode implements ErrorCode {

    FAILED("AUTH_001","Authentication failed"),
    UNAUTHORIZED("AUTH_002","Unauthorized"),
    JWT_INVALID("AUTH_003","Invalid JWT token"),
    JWT_EXPIRED("AUTH_004","JWT token expired");

    private final String code;
    private final String description;

}