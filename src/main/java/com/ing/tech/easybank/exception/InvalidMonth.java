package com.ing.tech.EasyBank.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_ACCEPTABLE)
public class InvalidMonth extends RuntimeException {
    public InvalidMonth(String message){
        super(message);
    }
}
