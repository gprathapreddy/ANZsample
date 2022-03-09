package com.ing.tech.EasyBank.service.helpers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateFormatter {
    public static String getRomanianDateFormatString(){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return LocalDateTime.now().format(dateTimeFormatter);
    }
}
