package com.plazoleta.notification_service.infrastructure.configuration;

import com.plazoleta.notification_service.domain.port.in.SendNotificationUseCase;
import com.plazoleta.notification_service.domain.port.out.RedisPort;
import com.plazoleta.notification_service.domain.service.DomainNotificationValidator;
import com.plazoleta.notification_service.domain.service.PinGenerator;
import com.plazoleta.notification_service.domain.service.SendNotificationService;
import com.plazoleta.notification_service.infrastructure.output.vonage.VonageSenderAdapter;
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
    public SendNotificationUseCase sendNotificationUseCase(
            RedisPort authSessionPort,
            VonageSenderAdapter twilioNotificationSenderAdapter,
            PinGenerator pinGenerator,
            DomainNotificationValidator domainNotificationValidator
    ) {
        return new SendNotificationService(
                authSessionPort,
                twilioNotificationSenderAdapter,
                pinGenerator,
                domainNotificationValidator
        );
    }

    @Bean
    public Duration authTokenExpiration(@Value("${notification.pin.expiration-minutes}") Long expirationMillis) {
        return Duration.ofMinutes(expirationMillis);
    }
}
