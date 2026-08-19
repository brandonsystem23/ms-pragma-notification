package com.plazoleta.notification_service.infrastructure.output.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plazoleta.notification_service.domain.model.AuthSession;
import com.plazoleta.notification_service.domain.model.NotificationData;
import com.plazoleta.notification_service.domain.port.out.RedisPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RedisAdapter implements RedisPort {

    private static final String PREFIX = "auth:token:";
    private static final String PREFIX_MESSAGE = "notification:pin:";

    private final ReactiveStringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final Duration expiration;

    @Override
    public Mono<AuthSession> findByToken(String token) {
        return redisTemplate.opsForValue()
                .get(PREFIX + token)
                .flatMap(this::deserialize);
    }

    @Override
    public Mono<String> save(String numberDocument, String pin, NotificationData notificationData) {
        String key = PREFIX_MESSAGE + numberDocument + pin;

        return serialize(notificationData)
                .flatMap(json -> redisTemplate.opsForValue().set(key, json, expiration))
                .flatMap(saved -> Boolean.TRUE.equals(saved)
                        ? Mono.just(pin)
                        : Mono.error(new IllegalStateException("No se pudo almacenar el PIN en Redis")));

    }

    private Mono<String> serialize(NotificationData notificationData) {
        try {
            return Mono.just(objectMapper.writeValueAsString(notificationData));
        } catch (JsonProcessingException e) {
            return Mono.error(new IllegalStateException("Error serializando el PIN", e));
        }
    }

    private Mono<AuthSession> deserialize(String json) {
        try {
            return Mono.just(objectMapper.readValue(json, AuthSession.class));
        } catch (JsonProcessingException e) {
            return Mono.error(new IllegalStateException("Error deserializando la sesión", e));
        }
    }
}