package com.plazoleta.notification_service.application.util;

import com.plazoleta.notification_service.infrastructure.util.Utils;
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
        String phone = "+573001234567";

        String normalized = Utils.normalizePhone(phone);

        assertEquals("573001234567", normalized);
    }

    @Test
    void shouldReturnSamePhoneWhenItDoesNotContainPlus() {
        String phone = "573001234567";

        String normalized = Utils.normalizePhone(phone);

        assertEquals("573001234567", normalized);
    }

    @Test
    void shouldThrowExceptionWhenPhoneIsNull() {
        assertThrows(
                IllegalArgumentException.class, () -> Utils.normalizePhone(null)
        );

    }
}
