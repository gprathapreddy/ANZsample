package com.ing.tech.EasyBank.dto;

import com.ing.tech.EasyBank.enums.TransactionTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MonthlySummaryDto {

    String month;
    String currency;
    Double income;
    Double expenses;
}
