package com.grootan.storeflow.utils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class CursorPaginationUtil {

    public static String encodeCursor(Object value) {
        if (value == null) return null;
        return Base64.getEncoder().encodeToString(value.toString().getBytes(StandardCharsets.UTF_8));
    }

    public static String decodeCursor(String cursor) {
        if (cursor == null || cursor.isEmpty()) return null;
        try {
            return new String(Base64.getDecoder().decode(cursor), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
