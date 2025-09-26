package org.gi.gICore.util;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{([^}]+)}");

    /**
     * 플레이스홀더 치환
     * 예: "Hello {name}!" -> "Hello Steve!"
     */
    public static String replacePlaceholders(String text, Map<String, String> placeholders) {
        if (text == null || placeholders == null) {
            return text;
        }

        Matcher matcher = PLACEHOLDER_PATTERN.matcher(text);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String key = matcher.group(1);
            String value = placeholders.getOrDefault(key, matcher.group(0));
            matcher.appendReplacement(result, Matcher.quoteReplacement(value));
        }

        matcher.appendTail(result);
        return result.toString();
    }

    /**
     * 문자열을 안전하게 자르기
     */
    public static String truncate(String str, int maxLength) {
        if (str == null) return null;
        if (str.length() <= maxLength) return str;
        return str.substring(0, maxLength - 3) + "...";
    }

    /**
     * 문자열 목록을 구분자로 연결
     */
    public static String join(List<String> strings, String delimiter) {
        return String.join(delimiter, strings);
    }

    /**
     * 문자열이 비어있는지 확인 (null-safe)
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * 문자열이 비어있지 않은지 확인 (null-safe)
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }


    public static String toCamelCase(String str) {
        if (isEmpty(str)) return str;

        String[] words = str.toLowerCase().split("[\\s_-]+");
        StringBuilder result = new StringBuilder(words[0]);

        for (int i = 1; i < words.length; i++) {
            if (!words[i].isEmpty()) {
                result.append(Character.toUpperCase(words[i].charAt(0)))
                        .append(words[i].substring(1));
            }
        }

        return result.toString();
    }

    public static String toSnakeCase(String str) {
        if (isEmpty(str)) return str;

        return str.replaceAll("([a-z])([A-Z])", "$1_$2")
                .toLowerCase()
                .replaceAll("\\s+", "_");
    }


    public static String colorize(String text) {
        if (text == null) return null;
        return text.replace('&', '§');
    }

    public static String decolorize(String text) {
        if (text == null) return null;
        return text.replaceAll("§[0-9a-fk-or]", "");
    }
}
