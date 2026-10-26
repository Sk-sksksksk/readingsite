package com.shashwat.readingsite.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Login details for the single owner account. Set OWNER_USERNAME and OWNER_PASSWORD
 * (or readingsite.owner.* in a local, git-ignored profile). If no password is set,
 * nobody can log in to /admin.
 */
@ConfigurationProperties(prefix = "readingsite.owner")
public record OwnerProperties(String username, String password) {
}
