package com.ing.tech.EasyBank.dto;

import com.ing.tech.EasyBank.entity.User;
import lombok.*;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AccountDto {
    @NotBlank
    private String accountNumber;

    private String currency;

    private Double balance;

    @NotBlank
    private String username;
}
