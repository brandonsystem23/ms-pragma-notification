package com.plazoleta.notification_service.domain.validation;

import com.plazoleta.notification_service.domain.exception.DomainErrorCode;
import com.plazoleta.notification_service.domain.exception.DomainErrorMessages;
import com.plazoleta.notification_service.domain.exception.DomainException;

public class DomainNotificationValidator {

    private static final int MAX_PHONE_LENGTH = 13;
    private static final String PHONE_REGEX = "^\\+?\\d+$";

    public void validatePhone(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new DomainException(
                    DomainErrorCode.VALIDATION_ERROR,
                    DomainErrorMessages.PHONE_REQUIRED
            );
        }

        if (phoneNumber.length() > MAX_PHONE_LENGTH) {
            throw new DomainException(
                    DomainErrorCode.VALIDATION_ERROR,
                    DomainErrorMessages.PHONE_MAX_LENGTH
            );
        }

        if (!phoneNumber.matches(PHONE_REGEX)) {
            throw new DomainException(
                    DomainErrorCode.VALIDATION_ERROR,
                    DomainErrorMessages.PHONE_INVALID
            );
        }
    }
}
