package com.plazoleta.notification_service.infrastructure.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {

    @Test
    void shouldExtractTokenSuccessfully() {
        String header = "Bearer my-token";

        String token = Utils.extract(header);

        assertEquals("my-token", token);
    }

    @Test
    void shouldThrowExceptionWhenAuthorizationHeaderIsNull() {
        assertThrows(
                IllegalArgumentException.class,
                () -> Utils.extract(null)
        );
    }

    @Test
    void shouldThrowExceptionWhenAuthorizationHeaderDoesNotStartWithBearer() {
        assertThrows(
                IllegalArgumentException.class, () -> Utils.extract("Basic abc123")
        );
    }

    @Test
    void shouldNormalizePhoneSuccessfully() {
        String phoneNumber = "+573001234567";

        String normalized = Utils.normalizePhone(phoneNumber);

        assertEquals("573001234567", normalized);
    }

    @Test
    void shouldReturnSamePhoneWhenItDoesNotContainPlus() {
        String phoneNumber = "573001234567";

        String normalized = Utils.normalizePhone(phoneNumber);

        assertEquals("573001234567", normalized);
    }

    @Test
    void shouldThrowExceptionWhenPhoneIsNull() {
        assertThrows(
                IllegalArgumentException.class, () -> Utils.normalizePhone(null)
        );
    }
}
