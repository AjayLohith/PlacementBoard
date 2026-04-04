package com.placementportal.constant;

import java.util.regex.Pattern;

public final class AppConstants {

    public static final Pattern COLLEGE_EMAIL =
            Pattern.compile(".+@vignaniit\\.edu\\.in$", Pattern.CASE_INSENSITIVE);

    private AppConstants() {
    }
}
