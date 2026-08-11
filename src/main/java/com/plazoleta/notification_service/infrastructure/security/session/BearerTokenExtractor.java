package com.plazoleta.notification_service.infrastructure.security.session;

public final class BearerTokenExtractor {

    private static final String BEARER_PREFIX = "Bearer ";

    private BearerTokenExtractor() {
    }

    public static String extract(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            throw new IllegalArgumentException("Authorization header inválido");
        }

        return authorizationHeader.substring(BEARER_PREFIX.length());
    }
}
