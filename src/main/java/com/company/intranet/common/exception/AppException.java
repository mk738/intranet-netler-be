package com.company.intranet.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AppException extends RuntimeException {

    private final ErrorCode  code;
    private final HttpStatus status;

    public AppException(ErrorCode code, String message, HttpStatus status) {
        super(message);
        this.code   = code;
        this.status = status;
    }
}
