package com.ing.tech.EasyBank.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
public class InvalidUsername extends RuntimeException{
    public InvalidUsername(String message) {
        super(message);
    }
}
