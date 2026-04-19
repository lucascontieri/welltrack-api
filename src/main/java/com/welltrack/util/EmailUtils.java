package com.welltrack.util;

import java.util.Locale;

public final class EmailUtils {

    private EmailUtils() {
    }

    public static String normalizar(String email) {
        if (email == null) {
            return null;
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
