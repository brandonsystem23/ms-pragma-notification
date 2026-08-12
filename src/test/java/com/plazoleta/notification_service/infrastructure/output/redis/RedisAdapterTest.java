package com.plazoleta.notification_service.infrastructure.output.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plazoleta.notification_service.domain.model.AuthSession;
import com.plazoleta.notification_service.domain.model.NotificationData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class RedisAdapterTest {

    @Mock
    private ReactiveStringRedisTemplate redisTemplate;

    @Mock
    private ReactiveValueOperations<String, String> valueOperations;

    @Mock
    private ObjectMapper objectMapper;

    private RedisAdapter redisAdapter;

    private final Duration expiration = Duration.ofMinutes(30);

    @BeforeEach
    void setUp() {
        redisAdapter = new RedisAdapter(redisTemplate, objectMapper, expiration);
    }

    @Test
    void shouldFindByTokenSuccessfully() throws Exception {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        String token = "valid-token";
        String json = """
                {
                  "userId":1,
                  "fullName":"Juan Perez",
                  "role":"EMPLEADO",
                  "numberDocument":"12345678",
                  "phone":"+573001234567",
                  "email":"juan@test.com"
                }
                """;

        AuthSession session = AuthSession.builder()
                .userId(1L)
                .fullName("Juan Perez")
                .role("EMPLEADO")
                .numberDocument("12345678")
                .phone("+573001234567")
                .email("juan@test.com")
                .build();

        when(valueOperations.get("auth:token:" + token)).thenReturn(Mono.just(json));
        when(objectMapper.readValue(json, AuthSession.class)).thenReturn(session);

        StepVerifier.create(redisAdapter.findByToken(token))
                .assertNext(result -> {
                    assertEquals(session.userId(), result.userId());
                    assertEquals(session.role(), result.role());
                    assertEquals(session.numberDocument(), result.numberDocument());
                })
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyWhenTokenDoesNotExist() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        String token = "missing-token";

        when(valueOperations.get("auth:token:" + token)).thenReturn(Mono.empty());

        StepVerifier.create(redisAdapter.findByToken(token))
                .verifyComplete();
    }

    @Test
    void shouldReturnErrorWhenDeserializationFails() throws Exception {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        String token = "valid-token";
        String json = "{\"invalid\":true}";

        when(valueOperations.get("auth:token:" + token)).thenReturn(Mono.just(json));
        when(objectMapper.readValue(json, AuthSession.class))
                .thenThrow(new JsonProcessingException("deserialize error") {});

        StepVerifier.create(redisAdapter.findByToken(token))
                .expectErrorMatches(error ->
                        error instanceof IllegalStateException &&
                                error.getMessage().equals("Error deserializando la sesión"))
                .verify();
    }

    @Test
    void shouldSaveNotificationDataSuccessfully() throws Exception {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        String numberDocument = "12345678";
        String pin = "123456";
        NotificationData notificationData = NotificationData.builder()
                .phone("+573001234567")
                .pin(pin)
                .build();

        String json = "{\"phone\":\"+573001234567\",\"pin\":\"123456\"}";
        String expectedKey = "notification:pin:" + numberDocument + pin;

        when(objectMapper.writeValueAsString(notificationData)).thenReturn(json);
        when(valueOperations.set(expectedKey, json, expiration)).thenReturn(Mono.just(true));

        StepVerifier.create(redisAdapter.save(numberDocument, pin, notificationData))
                .expectNext(pin)
                .verifyComplete();
    }

    @Test
    void shouldReturnErrorWhenRedisDoesNotSavePin() throws Exception {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        String numberDocument = "12345678";
        String pin = "123456";
        NotificationData notificationData = NotificationData.builder()
                .phone("+573001234567")
                .pin(pin)
                .build();

        String json = "{\"phone\":\"+573001234567\",\"pin\":\"123456\"}";
        String expectedKey = "notification:pin:" + numberDocument + pin;

        when(objectMapper.writeValueAsString(notificationData)).thenReturn(json);
        when(valueOperations.set(expectedKey, json, expiration)).thenReturn(Mono.just(false));

        StepVerifier.create(redisAdapter.save(numberDocument, pin, notificationData))
                .expectErrorMatches(error ->
                        error instanceof IllegalStateException &&
                                error.getMessage().equals("No se pudo almacenar el PIN en Redis"))
                .verify();
    }

    @Test
    void shouldReturnErrorWhenSerializationFails() throws Exception {
        String numberDocument = "12345678";
        String pin = "123456";
        NotificationData notificationData = NotificationData.builder()
                .phone("+573001234567")
                .pin(pin)
                .build();

        when(objectMapper.writeValueAsString(notificationData))
                .thenThrow(new JsonProcessingException("serialize error") {});

        StepVerifier.create(redisAdapter.save(numberDocument, pin, notificationData))
                .expectErrorMatches(error ->
                        error instanceof IllegalStateException &&
                                error.getMessage().equals("Error serializando el PIN"))
                .verify();
    }
}
