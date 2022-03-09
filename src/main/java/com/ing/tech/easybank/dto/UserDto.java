package com.ing.tech.EasyBank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@Data
public class UserDto {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
}
