package com.plazoleta.notification_service.domain.service;

import com.plazoleta.notification_service.domain.exception.DomainErrorCode;
import com.plazoleta.notification_service.domain.exception.DomainErrorMessages;
import com.plazoleta.notification_service.domain.exception.DomainException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DomainNotificationValidatorTest {

    private DomainNotificationValidator domainNotificationValidator;

    @BeforeEach
    void setUp() {
        domainNotificationValidator = new DomainNotificationValidator();
    }

    @Test
    void shouldPassWhenPhoneIsValid() {
        Assertions.assertDoesNotThrow(() ->
                domainNotificationValidator.validatePhone("+573001234567")
        );
    }

    @Test
    void shouldFailWhenPhoneIsNull() {
        DomainException exception = Assertions.assertThrows(
                DomainException.class,
                () -> domainNotificationValidator.validatePhone(null)
        );

        Assertions.assertEquals(DomainErrorCode.VALIDATION_ERROR, exception.getCode());
        Assertions.assertEquals(DomainErrorMessages.PHONE_REQUIRED, exception.getMessage());
    }

    @Test
    void shouldFailWhenPhoneIsBlank() {
        DomainException exception = Assertions.assertThrows(
                DomainException.class,
                () -> domainNotificationValidator.validatePhone("   ")
        );

        Assertions.assertEquals(DomainErrorCode.VALIDATION_ERROR, exception.getCode());
        Assertions.assertEquals(DomainErrorMessages.PHONE_REQUIRED, exception.getMessage());
    }

    @Test
    void shouldFailWhenPhoneExceedsMaxLength() {
        DomainException exception = Assertions.assertThrows(
                DomainException.class,
                () -> domainNotificationValidator.validatePhone("+1234567890123")
        );

        Assertions.assertEquals(DomainErrorCode.VALIDATION_ERROR, exception.getCode());
        Assertions.assertEquals(DomainErrorMessages.PHONE_MAX_LENGTH, exception.getMessage());
    }

    @Test
    void shouldFailWhenPhoneFormatIsInvalid() {
        DomainException exception = Assertions.assertThrows(
                DomainException.class,
                () -> domainNotificationValidator.validatePhone("ABC53001267")
        );

        Assertions.assertEquals(DomainErrorCode.VALIDATION_ERROR, exception.getCode());
        Assertions.assertEquals(DomainErrorMessages.PHONE_INVALID, exception.getMessage());
    }
}
