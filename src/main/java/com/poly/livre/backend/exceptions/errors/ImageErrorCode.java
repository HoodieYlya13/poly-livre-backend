package com.poly.livre.backend.exceptions.errors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public enum ImageErrorCode implements ErrorCode {

    IMAGE_NOT_FOUND("IMAGE.001", "Image with id %s not found");

    private final String code;
    private final String description;

}
