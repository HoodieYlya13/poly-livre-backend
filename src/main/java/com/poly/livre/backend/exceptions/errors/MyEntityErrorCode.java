package com.poly.livre.backend.exceptions.errors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public enum MyEntityErrorCode implements ErrorCode {

    NOT_FOUND("MY_ENTITY_001", "MyEntity not found {}");

    private final String code;
    private final String description;

}