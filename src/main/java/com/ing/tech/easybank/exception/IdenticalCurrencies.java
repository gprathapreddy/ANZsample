package com.ing.tech.EasyBank.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason="Accounts have identical currencies! In this case, please use transfer option!")
public class IdenticalCurrencies extends RuntimeException {
}
