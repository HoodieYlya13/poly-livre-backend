package com.poly.livre.backend.exceptions.errors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public enum JwtErrorCode implements ErrorCode {

    PRIVATE_KEY_INVALID("JWT_001", "Private key is invalid"),
    PUBLIC_KEY_INVALID("JWT_002", "Public key is invalid");

    private final String code;
    private final String description;

}