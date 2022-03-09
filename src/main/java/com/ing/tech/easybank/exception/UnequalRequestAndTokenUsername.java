package com.ing.tech.EasyBank.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
public class UnequalRequestAndTokenUsername extends RuntimeException {
    public UnequalRequestAndTokenUsername(String errorMessage) {
        super(errorMessage);
    }
}
