package com.shashwat.readingsite.service;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

/** Web-address helpers. Slugs are plain Latin letters, digits and hyphens. */
public final class Slugs {

    private static final Pattern VALID = Pattern.compile("[a-z0-9]+(?:-[a-z0-9]+)*");

    private Slugs() {
    }

    /** Builds a slug from text. Returns "" when nothing Latin is left (for example a Hindi title). */
    public static String from(String text, int maxLength) {
        if (text == null) {
            return "";
        }
        String plain = Normalizer.normalize(text, Normalizer.Form.NFKD).replaceAll("\\p{M}", "");
        String slug = plain.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-+|-+$", "");
        if (slug.length() > maxLength) {
            slug = slug.substring(0, maxLength).replaceAll("-+$", "");
        }
        return slug;
    }

    public static boolean isValid(String slug) {
        return slug != null && VALID.matcher(slug).matches();
    }
}
