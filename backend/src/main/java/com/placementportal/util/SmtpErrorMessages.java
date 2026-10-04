package com.placementportal.util;

public final class SmtpErrorMessages {

    private SmtpErrorMessages() {
    }

    public static String friendly(Throwable e) {
        if (e == null) {
            return "Unknown error";
        }
        String flat = flatten(e).toLowerCase();
        String specificError = extractSpecificMessage(e);

        if (flat.contains("535")
                || flat.contains("534")
                || flat.contains("authentication failed")
                || flat.contains("bad credentials")
                || flat.contains("not accepted")
                || flat.contains("username and password not accepted")) {
            
            StringBuilder sb = new StringBuilder();
            sb.append("SMTP login rejected (535 Authentication Failed). ");
            if (flat.contains("brevo") || flat.contains("smtp-brevo")) {
                sb.append("Brevo SMTP: Ensure EMAIL_USER_BREVO matches your Brevo SMTP login (e.g. 7xxxx@smtp-brevo.com or Brevo email), ")
                  .append("EMAIL_PASS_BREVO is your active Brevo SMTP key (xsmtpsib-...), ")
                  .append("and EMAIL_SENDER_BREVO is a verified sender in Brevo.");
            } else if (flat.contains("gmail") || flat.contains("google")) {
                sb.append("Gmail SMTP: Make sure you use a 16-character App Password (Google Account → Security → ")
                  .append("2-Step Verification → App passwords). Set EMAIL_USER to your full Gmail address and EMAIL_PASS to the app password.");
            } else {
                sb.append("Verify your SMTP settings in Render: ")
                  .append("If using Brevo: set EMAIL_USER_BREVO (SMTP login), EMAIL_PASS_BREVO (SMTP key), and EMAIL_SENDER_BREVO (verified sender). ")
                  .append("If using Gmail: set EMAIL_HOST=smtp.gmail.com, EMAIL_PORT=587, EMAIL_USER (Gmail address), and EMAIL_PASS (16-char App Password).");
            }
            if (specificError != null && !specificError.isBlank()) {
                sb.append(" [Server detail: ").append(specificError).append("]");
            }
            return sb.toString();
        }

        if (flat.contains("could not connect") || flat.contains("connection timed out") || flat.contains("timed out")) {
            return "Connection timed out. Render Free Tier blocks outbound SMTP traffic (ports 25, 465, 587, 2525). "
                    + "Please switch to Brevo's HTTP API: in Brevo, go to 'SMTP & API' → 'API Keys' tab → generate an API key (starts with 'xkeysib-'), "
                    + "and set it in Render as BREVO_API_KEY (or EMAIL_PASS_BREVO).";
        }

        if (flat.contains("must issue a starttls command") || flat.contains("starttls")) {
            return "STARTTLS negotiation failed. Ensure port 587 and STARTTLS are enabled."
                    + (specificError != null ? " [Detail: " + specificError + "]" : "");
        }

        if (specificError != null && !specificError.isBlank()) {
            return specificError;
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

    private static String extractSpecificMessage(Throwable e) {
        Throwable cur = e;
        while (cur != null) {
            String msg = cur.getMessage();
            if (msg != null && (msg.contains("535") || msg.contains("550") || msg.contains("534")
                    || msg.toLowerCase().contains("failed") || msg.toLowerCase().contains("rejected")
                    || msg.toLowerCase().contains("not accepted"))) {
                return msg.length() > 200 ? msg.substring(0, 200) + "..." : msg.trim();
            }
            cur = cur.getCause();
        }
        return null;
    }

    private static String flatten(Throwable e) {
        StringBuilder sb = new StringBuilder();
        Throwable cur = e;
        int depth = 0;
        while (cur != null && depth++ < 8) {
            if (cur.getMessage() != null) {
                sb.append(cur.getMessage()).append(' ');
            }
            sb.append(cur.getClass().getName()).append(' ');
            cur = cur.getCause();
        }
        return sb.toString();
    }
}
