package com.plazoleta.notification_service.infrastructure.util;

public final class Utils {

    private Utils() {
    }

    public static String normalizePhone(String phoneNumber) {

        if (phoneNumber == null) {
            throw new IllegalArgumentException("El teléfono no puede ser null");
        }

        return phoneNumber.replace("+", "");
    }
}
