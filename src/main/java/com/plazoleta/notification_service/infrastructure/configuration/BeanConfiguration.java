package com.plazoleta.notification_service.infrastructure.configuration;

import com.plazoleta.notification_service.domain.api.INotificationServicePort;
import com.plazoleta.notification_service.domain.validation.DomainNotificationValidator;
import com.plazoleta.notification_service.domain.validation.PinGenerator;
import com.plazoleta.notification_service.domain.validation.SendNotificationValidator;
import com.plazoleta.notification_service.domain.spi.INotificationCachePort;
import com.plazoleta.notification_service.domain.spi.INotificationSenderPort;
import com.plazoleta.notification_service.domain.usecase.SendNotificationUseCase;
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
    public SendNotificationValidator sendNotificationAuthorizationValidator(
            INotificationCachePort notificationCachePort
    ) {
        return new SendNotificationValidator(notificationCachePort);
    }

    @Bean
    public INotificationServicePort sendNotificationUseCase(
            INotificationCachePort iNotificationCachePort,
            INotificationSenderPort iNotificationSenderPort,
            PinGenerator pinGenerator,
            DomainNotificationValidator domainNotificationValidator,
            SendNotificationValidator sendNotificationValidator
    ) {
        return new SendNotificationUseCase(
                iNotificationCachePort,
                iNotificationSenderPort,
                pinGenerator,
                domainNotificationValidator,
                sendNotificationValidator
        );
    }

    @Bean
    public Duration authTokenExpiration(@Value("${notification.pin.expiration-minutes}") Long expirationMinutes) {
        return Duration.ofMinutes(expirationMinutes);
    }
}
