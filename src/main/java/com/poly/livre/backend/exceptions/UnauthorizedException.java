package com.poly.livre.backend.exceptions;

import com.poly.livre.backend.exceptions.errors.ErrorCode;
import lombok.Getter;

@Getter
public class UnauthorizedException extends ErrorCodeException {

    public UnauthorizedException(Throwable cause, ErrorCode errorCode, Object... args) {
        super(cause, errorCode, args);
    }

    public UnauthorizedException(ErrorCode errorCode, Object... args) {
        super(errorCode, args);
    }

}