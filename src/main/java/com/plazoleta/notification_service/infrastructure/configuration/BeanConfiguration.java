package com.plazoleta.notification_service.infrastructure.configuration;

import com.plazoleta.notification_service.domain.port.in.SendNotificationUseCase;
import com.plazoleta.notification_service.domain.port.out.AuthSessionPort;
import com.plazoleta.notification_service.domain.port.out.PinRepositoryPort;
import com.plazoleta.notification_service.domain.service.PinGenerator;
import com.plazoleta.notification_service.domain.service.SendNotificationService;
import com.plazoleta.notification_service.infrastructure.output.notification.NotificationSenderResolver;
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
    public SendNotificationUseCase sendNotificationUseCase(
            AuthSessionPort authSessionPort,
            PinRepositoryPort pinRepositoryPort,
            NotificationSenderResolver notificationSenderResolver,
            PinGenerator pinGenerator
    ) {
        return new SendNotificationService(
                authSessionPort,
                pinRepositoryPort,
                notificationSenderResolver,
                pinGenerator
        );
    }

    @Bean
    public Duration authTokenExpiration(@Value("${notification.pin.expiration-minutes}") Long expirationMillis) {
        return Duration.ofMinutes(expirationMillis);
    }
}
