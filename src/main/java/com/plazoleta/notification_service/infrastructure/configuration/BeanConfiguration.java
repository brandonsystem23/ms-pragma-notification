package com.plazoleta.notification_service.infrastructure.configuration;

import com.plazoleta.notification_service.domain.api.INotificationServicePort;
import com.plazoleta.notification_service.domain.spi.INotificationCachePort;
import com.plazoleta.notification_service.domain.spi.INotificationSenderPort;
import com.plazoleta.notification_service.domain.usecase.SendNotificationUseCase;
import com.plazoleta.notification_service.domain.validation.DomainNotificationValidator;
import com.plazoleta.notification_service.domain.validation.PinGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class BeanConfiguration {

    @Bean
    public PinGenerator pinGenerator(@Value("${notification.pin.length}") int length) {
        return new PinGenerator(length);
    }

    @Bean
    public DomainNotificationValidator domainNotificationValidator() {
        return new DomainNotificationValidator();
    }

    @Bean
    public INotificationServicePort sendNotificationUseCase(
            INotificationCachePort iNotificationCachePort,
            INotificationSenderPort iNotificationSenderPort,
            PinGenerator pinGenerator,
            DomainNotificationValidator domainNotificationValidator
    ) {
        return new SendNotificationUseCase(
                iNotificationCachePort,
                iNotificationSenderPort,
                pinGenerator,
                domainNotificationValidator
        );
    }

    @Bean
    public Duration expiration(@Value("${notification.pin.expiration-minutes}") Long expirationMinutes) {
        return Duration.ofMinutes(expirationMinutes);
    }
}
