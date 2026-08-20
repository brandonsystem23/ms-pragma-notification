package com.plazoleta.notification_service.infrastructure.util;

public final class Utils {

    private static final String BEARER_PREFIX = "Bearer ";

    private Utils() {
    }

    public static String extract(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            throw new IllegalArgumentException("Authorization header inválido");
        }

        return authorizationHeader.substring(BEARER_PREFIX.length());
    }

    public static String normalizePhone(String phoneNumber) {

        if (phoneNumber == null) {
            throw new IllegalArgumentException("El teléfono no puede ser null");
        }

        return phoneNumber.replace("+", "");
    }
}
