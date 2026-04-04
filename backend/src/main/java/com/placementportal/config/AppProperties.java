package com.placementportal.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private String[] adminEmails = new String[]{};
    private String[] corsAllowedOrigins = new String[]{};

    public String[] getAdminEmails() {
        return adminEmails;
    }

    public void setAdminEmails(String[] adminEmails) {
        this.adminEmails = adminEmails;
    }

    public String[] getCorsAllowedOrigins() {
        return corsAllowedOrigins;
    }

    public void setCorsAllowedOrigins(String[] corsAllowedOrigins) {
        this.corsAllowedOrigins = corsAllowedOrigins;
    }
}
