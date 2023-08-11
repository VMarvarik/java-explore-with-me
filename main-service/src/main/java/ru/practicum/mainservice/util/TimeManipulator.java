package ru.practicum.mainservice.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeManipulator {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String formatTimeToString(LocalDateTime time) {
        return time.format(formatter);
    }

    public static LocalDateTime formatStringToTime(String time) {
        return LocalDateTime.parse(time, formatter);
    }
}
