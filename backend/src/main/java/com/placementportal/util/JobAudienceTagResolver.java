package com.placementportal.util;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Derives FRESHERS vs EXPERIENCED from free-text fields when not set explicitly. */
public final class JobAudienceTagResolver {

    public static final String FRESHERS = "FRESHERS";
    public static final String EXPERIENCED = "EXPERIENCED";

    private static final Pattern YEAR = Pattern.compile("\\b(20\\d{2})\\b");
    private static final Pattern EXP_YEARS =
            Pattern.compile("(\\d+)\\s*\\+?\\s*(year|years|yr|yrs)\\b", Pattern.CASE_INSENSITIVE);

    private JobAudienceTagResolver() {}

    /** Normalize explicit tag from API or AI; null if invalid. */
    public static String normalizeExplicit(String tag) {
        if (tag == null || tag.isBlank()) {
            return null;
        }
        String t = tag.trim().toUpperCase();
        if (FRESHERS.equals(t) || EXPERIENCED.equals(t)) {
            return t;
        }
        return null;
    }

    /**
     * Heuristic: explicit tag wins; else fresher keywords; else cohort years 2024–2026 (or &lt; 2027); else
     * experience-year patterns → EXPERIENCED; default EXPERIENCED.
     */
    public static String resolve(
            String explicitTag,
            String experienceText,
            String passoutYear,
            String qualificationYear) {
        String normalized = normalizeExplicit(explicitTag);
        if (normalized != null) {
            return normalized;
        }

        String exp = experienceText != null ? experienceText.toLowerCase() : "";
        if (exp.contains("fresher")
                || exp.contains("fresh graduate")
                || exp.contains("fresh grad")
                || exp.contains("entry-level")
                || exp.equals("fresher")) {
            return FRESHERS;
        }

        Set<Integer> years = new HashSet<>();
        years.addAll(extractYears(passoutYear));
        years.addAll(extractYears(qualificationYear));
        years.addAll(extractYears(experienceText));
        for (int y : years) {
            if (y >= 2024 && y <= 2026) {
                return FRESHERS;
            }
        }

        if (EXP_YEARS.matcher(exp).find()) {
            return EXPERIENCED;
        }
        if (exp.contains("senior")
                || exp.contains("lead ")
                || exp.contains("manager")
                || exp.contains("principal")
                || exp.contains("staff ")
                || exp.contains("years of experience")) {
            return EXPERIENCED;
        }

        if (!exp.isBlank() && !exp.contains("fresher")) {
            Matcher m = Pattern.compile("(\\d+)\\s*\\+?\\s*years?").matcher(exp);
            if (m.find()) {
                try {
                    int n = Integer.parseInt(m.group(1));
                    if (n >= 1) {
                        return EXPERIENCED;
                    }
                } catch (NumberFormatException ignored) {
                    // fall through
                }
            }
        }

        return EXPERIENCED;
    }

    private static Set<Integer> extractYears(String text) {
        Set<Integer> out = new HashSet<>();
        if (text == null || text.isBlank()) {
            return out;
        }
        Matcher m = YEAR.matcher(text);
        while (m.find()) {
            try {
                out.add(Integer.parseInt(m.group(1)));
            } catch (NumberFormatException ignored) {
                // skip
            }
        }
        return out;
    }
}
