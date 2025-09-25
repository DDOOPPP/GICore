package org.gi.gICore.util;


import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtil {
    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 현재 시간 반환
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    /**
     * 시간을 문자열로 포맷
     */
    public static String format(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DEFAULT_FORMATTER) : null;
    }

    public static String format(LocalDateTime dateTime, String pattern) {
        return dateTime != null ? dateTime.format(DateTimeFormatter.ofPattern(pattern)) : null;
    }

    /**
     * 시간 차이를 사람이 읽기 쉬운 형태로 변환
     */
    public static String formatDuration(Duration duration) {
        if (duration == null) return "알 수 없음";

        long seconds = duration.getSeconds();
        long days = seconds / 86400;
        long hours = (seconds % 86400) / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;

        if (days > 0) {
            return String.format("%d일 %d시간", days, hours);
        } else if (hours > 0) {
            return String.format("%d시간 %d분", hours, minutes);
        } else if (minutes > 0) {
            return String.format("%d분 %d초", minutes, secs);
        } else {
            return String.format("%d초", secs);
        }
    }

    /**
     * 두 시간 사이의 차이 계산
     */
    public static Duration between(LocalDateTime start, LocalDateTime end) {
        return Duration.between(start, end);
    }

    /**
     * 틱을 시간으로 변환 (1초 = 20틱)
     */
    public static long ticksToSeconds(long ticks) {
        return ticks / 20;
    }

    public static long secondsToTicks(long seconds) {
        return seconds * 20;
    }
}
