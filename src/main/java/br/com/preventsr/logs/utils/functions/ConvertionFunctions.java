package br.com.preventsr.logs.utils.functions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ConvertionFunctions {
    public static String convertDateInCode(LocalDateTime localDateTime) {
        return String.valueOf(localDateTime.getYear() + localDateTime.getMonthValue() + localDateTime.getDayOfMonth() + localDateTime.getHour() + localDateTime.getMinute() + localDateTime.getSecond() + localDateTime.getNano());
    }

    public static LocalDateTime convertStringInLocalDateTime(String stringDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        return LocalDateTime.parse(stringDate, formatter);

    }
}
