package com.poly.livre.backend.exceptions.errors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public enum UserErrorCode implements ErrorCode {

    NOT_FOUND("USER_001", "User not found");

    private final String code;
    private final String description;

}