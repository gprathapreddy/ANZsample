package com.ing.tech.EasyBank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ExchangeDtoOutput {
    String fromCurrency;

    String toCurrency;

    Double amountFrom;

    Double amountTo;

    Double rate;

    String date;
}
