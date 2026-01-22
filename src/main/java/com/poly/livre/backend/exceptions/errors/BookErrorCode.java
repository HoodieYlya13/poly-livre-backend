package com.poly.livre.backend.exceptions.errors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public enum BookErrorCode implements ErrorCode {

    BOOK_NOT_FOUND("BOOK.001", "Book with id %s not found");

    private final String code;
    private final String description;

}
