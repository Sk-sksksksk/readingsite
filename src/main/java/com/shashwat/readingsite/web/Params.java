package com.shashwat.readingsite.web;

final class Params {

    private Params() {
    }

    /** Reads an enum from a query parameter; blank or unknown values give null. */
    static <E extends Enum<E>> E parseEnum(Class<E> type, String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Enum.valueOf(type, value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
