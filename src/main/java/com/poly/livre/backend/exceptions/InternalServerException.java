package com.poly.livre.backend.exceptions;

import com.poly.livre.backend.exceptions.errors.ErrorCode;
import lombok.Getter;

@Getter
public class InternalServerException extends ErrorCodeException {

    public InternalServerException(Throwable cause, ErrorCode errorCode, Object... args) {
        super(cause, errorCode, args);
    }

    public InternalServerException(ErrorCode errorCode, Object... args) {
        super(errorCode, args);
    }

}
