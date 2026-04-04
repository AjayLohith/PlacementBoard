package com.placementportal.util;

public final class SlugUtil {

    private SlugUtil() {
    }

    public static String slugify(String title) {
        if (title == null || title.isBlank()) {
            return "article";
        }
        String s = title.trim().toLowerCase().replaceAll("[^a-z0-9\\s-]", "").replaceAll("\\s+", "-");
        s = s.replaceAll("-+", "-");
        if (s.startsWith("-")) {
            s = s.substring(1);
        }
        if (s.endsWith("-")) {
            s = s.substring(0, s.length() - 1);
        }
        return s.isEmpty() ? "article" : s;
    }
}
