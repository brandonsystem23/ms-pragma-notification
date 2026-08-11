package com.plazoleta.notification_service.infrastructure.output.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plazoleta.notification_service.domain.model.auth.AuthSession;
import com.plazoleta.notification_service.domain.port.out.AuthSessionPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class RedisAuthSessionAdapter implements AuthSessionPort {

    private static final String PREFIX = "auth:token:";

    private final ReactiveStringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<AuthSession> findByToken(String token) {
        return redisTemplate.opsForValue()
                .get(PREFIX + token)
                .flatMap(this::deserialize);
    }

    private Mono<AuthSession> deserialize(String json) {
        try {
            return Mono.just(objectMapper.readValue(json, AuthSession.class));
        } catch (JsonProcessingException e) {
            return Mono.error(new IllegalStateException("Error deserializando la sesión", e));
        }
    }
}