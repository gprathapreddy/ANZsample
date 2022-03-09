package com.ing.tech.EasyBank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ExchangeDtoInput {
    @NotBlank
    String fromAccountNumber;
    @NotBlank
    String toAccountNumber;
    @Positive
    Double amount;  // can primitive be used as well?
}
