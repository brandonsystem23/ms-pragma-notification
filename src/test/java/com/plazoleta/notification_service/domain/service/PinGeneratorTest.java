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
    void shouldGenerateDifferentPins() {
        PinGenerator pinGenerator = new PinGenerator(6);

        String pin1 = pinGenerator.generate();
        String pin2 = pinGenerator.generate();

        assertNotNull(pin1);
        assertNotNull(pin2);
        assertEquals(6, pin1.length());
        assertEquals(6, pin2.length());
        assertTrue(pin1.matches("\\d{6}"));
        assertTrue(pin2.matches("\\d{6}"));
    }

    @Test
    void shouldThrowExceptionWhenLengthIsLessThanMinimum() {
        InvalidPinException exception = assertThrows(
                InvalidPinException.class,
                () -> new PinGenerator(3)
        );

        assertEquals("La longitud del PIN dede ser entre 4 y 6", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenLengthIsGreaterThanMaximum() {
        InvalidPinException exception = assertThrows(
                InvalidPinException.class,
                () -> new PinGenerator(7)
        );

        assertEquals("La longitud del PIN dede ser entre 4 y 6", exception.getMessage());
    }
}
