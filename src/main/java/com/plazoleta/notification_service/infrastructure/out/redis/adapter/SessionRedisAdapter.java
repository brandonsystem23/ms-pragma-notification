package com.plazoleta.notification_service.infrastructure.out.redis.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plazoleta.notification_service.domain.model.AuthSession;
import com.plazoleta.notification_service.domain.model.NotificationData;
import com.plazoleta.notification_service.infrastructure.out.redis.dto.NotificationRedisValue;
import com.plazoleta.notification_service.domain.spi.INotificationCachePort;
import com.plazoleta.notification_service.infrastructure.out.redis.dto.AuthSessionRedisValue;
import com.plazoleta.notification_service.infrastructure.out.redis.mapper.RedisRequestMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
public class SessionRedisAdapter implements INotificationCachePort {

    private static final String PREFIX = "auth:token:";
    private static final String PREFIX_MESSAGE = "notification:pin:";

    private final ReactiveStringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final Duration expiration;
    private final RedisRequestMapper redisRequestMapper;

    @Override
    public Mono<AuthSession> findByToken(String token) {
        return redisTemplate.opsForValue()
                .get(PREFIX + token)
                .flatMap(this::deserialize)
                .map(redisRequestMapper::toDomain);
    }

    @Override
    public Mono<String> save(String numberDocument, String pin, NotificationData notificationData) {
        String key = PREFIX_MESSAGE + numberDocument + pin;
        NotificationRedisValue redisValue = redisRequestMapper.toInsert(notificationData);

        log.info("Guardando PIN en REDIS para {}", notificationData.phoneNumber());

        return serialize(redisValue)
                .flatMap(json -> redisTemplate.opsForValue().set(key, json, expiration))
                .flatMap(saved -> {
                    if(Boolean.TRUE.equals(saved)) {
                        log.info("PIN registrado en REDIS exitosamente para {}", notificationData.phoneNumber());
                        return Mono.just(pin);
                    }

                    log.error("Error al registrar el PIN en REDIS  para {}", notificationData.phoneNumber());
                    return Mono.error(new IllegalStateException("No se pudo almacenar el PIN en Redis para " +
                            notificationData.phoneNumber()));
                });
    }

    private Mono<String> serialize(NotificationRedisValue notificationData) {
        try {
            return Mono.just(objectMapper.writeValueAsString(notificationData));
        } catch (JsonProcessingException e) {
            return Mono.error(new IllegalStateException("Error serializando el PIN", e));
        }
    }

    private Mono<AuthSessionRedisValue> deserialize(String json) {
        try {
            return Mono.just(objectMapper.readValue(json, AuthSessionRedisValue.class));
        } catch (JsonProcessingException e) {
            return Mono.error(new IllegalStateException("Error deserializando la sesión", e));
        }
    }
}
