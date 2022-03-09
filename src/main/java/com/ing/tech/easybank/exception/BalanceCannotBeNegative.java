package com.ing.tech.EasyBank.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason="Balance cannot be negative !")
public class BalanceCannotBeNegative extends RuntimeException{
}
