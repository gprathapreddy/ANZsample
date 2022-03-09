package com.ing.tech.EasyBank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TransactionDto {

    @NotBlank
    private String fromAccountNumber;

    @NotBlank
    private String toAccountNumber;

    @Min(0)
    private Double amount;

    private String date;    // ??


}
