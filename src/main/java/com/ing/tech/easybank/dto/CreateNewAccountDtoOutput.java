package com.ing.tech.EasyBank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateNewAccountDtoOutput {
    private String username;
    private String accountNumber;
    private String currency;
    private double balance;
}
