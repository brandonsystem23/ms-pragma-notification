package com.plazoleta.notification_service.infrastructure.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {

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
