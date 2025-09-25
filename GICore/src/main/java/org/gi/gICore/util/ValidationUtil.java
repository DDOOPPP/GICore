package org.gi.gICore.util;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Predicate;

public class ValidationUtil {
    public static void requireNonNull(Object obj, String message) {
        if (obj == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void requireNonEmpty(String str, String message) {
        if (str == null || str.trim().isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void requireNonEmpty(Collection<?> collection, String message) {
        if (collection == null || collection.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void require(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }

    public static <T> void require(T value, Predicate<T> condition, String message) {
        if (!condition.test(value)) {
            throw new IllegalArgumentException(message);
        }
    }

    public static boolean isValidUUID(String uuid) {
        try {
            UUID.fromString(uuid);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    public static boolean isPositive(Number number) {
        return number != null && number.doubleValue() > 0;
    }

    public static boolean isNonNegative(Number number) {
        return number != null && number.doubleValue() >= 0;
    }

    public static boolean isInRange(Number value, Number min, Number max) {
        if (value == null || min == null || max == null) {
            return false;
        }
        double val = value.doubleValue();
        double minVal = min.doubleValue();
        double maxVal = max.doubleValue();
        return val >= minVal && val <= maxVal;
    }
}