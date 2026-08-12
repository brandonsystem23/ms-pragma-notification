package com.plazoleta.notification_service.domain.service;

import com.plazoleta.notification_service.domain.exception.InvalidPinException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PinGeneratorTest {

    @Test
    void shouldGeneratePinWithExpectedLength() {
        PinGenerator pinGenerator = new PinGenerator(6);

        String pin = pinGenerator.generate();
        assertNotNull(pin);
        assertEquals(6, pin.length());
        assertTrue(pin.matches("\\d{6}"));
    }


    @Test
    void shouldThrowExceptionWhenLengthIsLessThanMinimum() {
        assertThrows(
                InvalidPinException.class, () -> new PinGenerator(3)
        );

    }

    @Test
    void shouldThrowExceptionWhenLengthIsGreaterThanMaximum() {
        assertThrows(
                InvalidPinException.class, () -> new PinGenerator(7)
        );

    }
}
