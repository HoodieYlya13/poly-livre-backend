package com.poly.livre.backend.exceptions;

import com.poly.livre.backend.exceptions.errors.ErrorCode;
import lombok.Getter;

@Getter
public class BadRequestException extends ErrorCodeException {

    public BadRequestException(Throwable cause, ErrorCode errorCode, Object... args) {
        super(cause, errorCode, args);
    }

    public BadRequestException(ErrorCode errorCode, Object... args) {
        super(errorCode, args);
    }

}