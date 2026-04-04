package com.placementportal.util;

public final class SmtpErrorMessages {

    private SmtpErrorMessages() {
    }

    public static String friendly(Throwable e) {
        if (e == null) {
            return "Unknown error";
        }
        String flat = flatten(e).toLowerCase();
        if (flat.contains("535")
                || flat.contains("534")
                || flat.contains("authentication failed")
                || flat.contains("not accepted")) {
            return "Gmail rejected the login. Use a 16-character App Password (Google Account → Security → "
                    + "2-Step Verification → App passwords), set EMAIL_USER to your Gmail address, and EMAIL_PASS "
                    + "to the app password (spaces are OK—they are removed automatically).";
        }
        if (flat.contains("could not connect") || flat.contains("connection timed out") || flat.contains("timed out")) {
            return "Could not reach the mail server. Check firewall/VPN, port 587, and EMAIL_HOST.";
        }
        if (flat.contains("must issue a starttls command")) {
            return "STARTTLS failed. Ensure port 587 and TLS are enabled (already configured in the app).";
        }
        String shortMsg = e.getMessage();
        if (shortMsg != null && !shortMsg.isBlank() && shortMsg.length() < 220) {
            return shortMsg.trim();
        }
        Throwable c = e.getCause();
        if (c != null && c.getMessage() != null && !c.getMessage().isBlank()) {
            return c.getMessage().trim();
        }
        return e.getClass().getSimpleName();
    }

    private static String flatten(Throwable e) {
        StringBuilder sb = new StringBuilder();
        Throwable cur = e;
        int depth = 0;
        while (cur != null && depth++ < 8) {
            if (cur.getMessage() != null) {
                sb.append(cur.getMessage()).append(' ');
            }
            cur = cur.getCause();
        }
        return sb.toString();
    }
}
