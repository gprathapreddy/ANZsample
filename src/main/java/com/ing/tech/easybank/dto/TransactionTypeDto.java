package com.ing.tech.EasyBank.dto;

import com.ing.tech.EasyBank.enums.TransactionTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TransactionTypeDto {

    LocalDateTime date;
    Long transactionId;
//    String from;
//    String to;
    String toFrom;
    Double amount;
    String currency;
    TransactionTypeEnum type;
}
