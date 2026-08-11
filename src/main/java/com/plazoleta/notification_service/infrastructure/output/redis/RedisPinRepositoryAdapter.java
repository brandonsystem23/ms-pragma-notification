package com.plazoleta.notification_service.infrastructure.output.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plazoleta.notification_service.domain.model.NotificationMessage;
import com.plazoleta.notification_service.domain.port.out.PinRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RedisPinRepositoryAdapter implements PinRepositoryPort {

    private static final String PREFIX = "notification:pin:";

    private final ReactiveStringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final Duration expiration;

    @Override
    public Mono<String> save(String phone, String pin, NotificationMessage notificationMessage) {
        String key = PREFIX + pin;

        return serialize(notificationMessage)
                .flatMap(json -> redisTemplate.opsForValue().set(key, json, expiration))
                .flatMap(saved -> Boolean.TRUE.equals(saved)
                        ? Mono.just(pin)
                        : Mono.error(new IllegalStateException("No se pudo almacenar el PIN en Redis")));

    }

    private Mono<String> serialize(NotificationMessage notificationMessage) {
        try {
            return Mono.just(objectMapper.writeValueAsString(notificationMessage));
        } catch (JsonProcessingException e) {
            return Mono.error(new IllegalStateException("Error serializando el PIN", e));
        }
    }
}
