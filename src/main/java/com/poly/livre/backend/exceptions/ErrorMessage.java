package com.poly.livre.backend.exceptions;

import com.poly.livre.backend.exceptions.errors.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
public class ErrorMessage implements ErrorCode {

    private final String code;
    private final String description;
    private final OffsetDateTime timestamp;

    public static ErrorMessage fromErrorCodeException(ErrorCodeException ex) {
        return new ErrorMessage(
                ex.getErrorCode().getCode(),
                ex.getErrorCode().getDescription(),
                OffsetDateTime.now());
    }

    public static ErrorMessage fromErrorCode(ErrorCode errorCode, Object... args) {
        return new ErrorMessage(
                errorCode.getCode(),
                String.format(errorCode.getDescription(), args),
                OffsetDateTime.now());
    }

}