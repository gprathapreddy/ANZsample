package com.ing.tech.EasyBank.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason="Insufficient funds !")  // ISN'T 406(NOT ACCEPTABLE) better?
public class InsufficientFunds extends RuntimeException{
}
