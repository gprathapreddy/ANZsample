package com.ing.tech.EasyBank.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class AccountAlreadyExists extends RuntimeException{
    public AccountAlreadyExists(String message) {
        super(message);
    }
}
