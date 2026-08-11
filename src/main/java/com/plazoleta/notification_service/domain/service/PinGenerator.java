package com.plazoleta.notification_service.domain.service;

import java.security.SecureRandom;

public class PinGenerator {

    private final SecureRandom secureRandom = new SecureRandom();
    private final int length;

    public PinGenerator(int length) {
        this.length = length;
    }

    public String generate() {
        int bound = (int) Math.pow(10, length);
        int min = (int) Math.pow(10, length - 1);
        int value = secureRandom.nextInt(bound - min) + min;
        return String.valueOf(value);
    }
}
