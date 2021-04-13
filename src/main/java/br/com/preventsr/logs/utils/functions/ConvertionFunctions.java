package br.com.preventsr.logs.utils.functions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class ConvertionFunctions {
    public static String convertDateInCode(LocalDateTime localDateTime) {
        return String.valueOf(localDateTime.getYear() + localDateTime.getMonthValue() + localDateTime.getDayOfMonth() + localDateTime.getHour() + localDateTime.getMinute() + localDateTime.getSecond() + localDateTime.getNano());
    }

    public static LocalDateTime convertStringInLocalDateTime(String stringDate) {
        String[] split = stringDate.split(Pattern.quote("."));
        DateTimeFormatter formatter;
        if (split[1].split("").length > 3) {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
        } else {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        }
        return LocalDateTime.parse(stringDate, formatter);

    }
}
