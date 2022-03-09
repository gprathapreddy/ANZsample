package com.ing.tech.EasyBank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RequestReceivedDtoOutput {
    Long id;
    String receiver;
    String sender;
    String transferAccount;
    Double amount;
    String currency;
    String message;
}
