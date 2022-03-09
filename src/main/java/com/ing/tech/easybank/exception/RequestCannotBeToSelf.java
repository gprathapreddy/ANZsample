package com.ing.tech.EasyBank.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class RequestCannotBeToSelf extends RuntimeException{
    public RequestCannotBeToSelf(String message) {
        super(message);
    }
}
