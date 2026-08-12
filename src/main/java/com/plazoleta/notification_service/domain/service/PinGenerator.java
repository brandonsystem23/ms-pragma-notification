package com.plazoleta.notification_service.domain.service;


import com.plazoleta.notification_service.domain.exception.InvalidPinException;

import java.security.SecureRandom;

public final class PinGenerator {

    private static final int MIN_PIN_LENGTH = 4;
    private static final int MAX_PIN_LENGTH = 6;
    private static final int DECIMAL_RADIX = 10;

    private final SecureRandom secureRandom;
    private final int length;

    public PinGenerator(int length) {
        validateLength(length);
        this.length = length;
        this.secureRandom = new SecureRandom();
    }

    public String generate() {
        StringBuilder pin = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            pin.append(secureRandom.nextInt(DECIMAL_RADIX));
        }

        return pin.toString();
    }

    private static void validateLength(int length) {
        if (length < MIN_PIN_LENGTH || length > MAX_PIN_LENGTH) {
            throw new InvalidPinException(MIN_PIN_LENGTH, MAX_PIN_LENGTH);
        }
    }
}

