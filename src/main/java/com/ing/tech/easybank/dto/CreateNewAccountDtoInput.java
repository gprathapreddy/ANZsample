package com.ing.tech.EasyBank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateNewAccountDtoInput {
    @NotBlank
    private String currency;

    private double balance;
}
