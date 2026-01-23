package com.poly.livre.backend.exceptions.errors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public enum UserErrorCode implements ErrorCode {

    NOT_FOUND("USER.001", "User not found"),
    USERNAME_MISMATCH("USER.002", "Username mismatch"),
    INVALID_STATUS("USER.003", "Invalid status: %s"),
    ACCESS_DENIED("USER.004", "Access denied");

    private final String code;
    private final String description;

}