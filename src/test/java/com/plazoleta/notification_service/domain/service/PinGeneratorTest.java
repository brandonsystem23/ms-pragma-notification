package com.plazoleta.notification_service.domain.service;

import com.plazoleta.notification_service.domain.exception.DomainErrorCode;
import com.plazoleta.notification_service.domain.exception.DomainErrorMessages;
import com.plazoleta.notification_service.domain.exception.DomainException;
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
        DomainException exception = assertThrows(
                DomainException.class, () -> new PinGenerator(3)
        );

        assertEquals(DomainErrorCode.VALIDATION_ERROR, exception.getCode());
        assertEquals(DomainErrorMessages.PIN_LENGTH_INVALID, exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenLengthIsGreaterThanMaximum() {
        DomainException exception = assertThrows(
                DomainException.class, () -> new PinGenerator(7)
        );

        assertEquals(DomainErrorCode.VALIDATION_ERROR, exception.getCode());
        assertEquals(DomainErrorMessages.PIN_LENGTH_INVALID, exception.getMessage());
    }
}
