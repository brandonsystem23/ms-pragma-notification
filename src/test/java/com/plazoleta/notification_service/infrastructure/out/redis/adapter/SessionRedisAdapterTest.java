package com.plazoleta.notification_service.infrastructure.out.redis.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plazoleta.notification_service.domain.model.AuthSession;
import com.plazoleta.notification_service.domain.model.NotificationData;
import com.plazoleta.notification_service.infrastructure.out.redis.dto.AuthSessionRedisValue;
import com.plazoleta.notification_service.infrastructure.out.redis.dto.NotificationRedisValue;
import com.plazoleta.notification_service.infrastructure.out.redis.mapper.RedisRequestMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionRedisAdapterTest {

    @Mock
    private ReactiveStringRedisTemplate redisTemplate;

    @Mock
    private ReactiveValueOperations<String, String> valueOperations;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private Duration duration;

    @Mock
    private RedisRequestMapper redisRequestMapper;

    @InjectMocks
    private SessionRedisAdapter redisAdapter;

    @Test
    void shouldFindByTokenSuccessfully() throws Exception {
        AuthSessionRedisValue session = AuthSessionRedisValue.builder()
                .userId(1L)
                .role("EMPLEADO")
                .numberDocument("123456")
                .phone("+573001112233")
                .email("admin@test.com")
                .build();

        AuthSession authSession = AuthSession.builder()
                .userId(1L)
                .role("EMPLEADO")
                .numberDocument("123456")
                .phone("+573001112233")
                .email("admin@test.com")
                .build();

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(any())).thenReturn(Mono.just("{}"));
        when(objectMapper.readValue(anyString(), eq(AuthSessionRedisValue.class))).thenReturn(session);
        when(redisRequestMapper.toDomain(any())).thenReturn(authSession);

        StepVerifier.create(redisAdapter.findByToken("test-token"))
                .assertNext(found -> {
                    Assertions.assertEquals(1L, found.userId());
                    Assertions.assertEquals("EMPLEADO", found.role());
                    Assertions.assertEquals("123456", found.numberDocument());
                })
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyWhenTokenDoesNotExist() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(redisAdapter.findByToken("missing-token"))
                .verifyComplete();
    }

    @Test
    void shouldReturnDomainErrorWhenDeserializationFails() throws JsonProcessingException {
        JsonProcessingException exception =
                new JsonProcessingException("Error de deserialización") {};

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(anyString())).thenReturn(Mono.just("{}"));
        when(objectMapper.readValue(anyString(), eq(AuthSessionRedisValue.class))).thenThrow(exception);

        StepVerifier.create(redisAdapter.findByToken("test-token"))
                .expectErrorMatches(error ->
                        error instanceof IllegalStateException &&
                                error.getMessage().equals("Error deserializando la sesión"))
                .verify();
    }

    @Test
    void shouldSaveNotificationDataSuccessfully() throws Exception {
        String pin = "123456";
        String documentNumber = "77019939";

        NotificationData notificationData = NotificationData.builder()
                .phoneNumber("+573001234567")
                .pin(pin)
                .build();

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");
        when(valueOperations.set(anyString(), anyString(), any(Duration.class)))
                .thenReturn(Mono.just(true));

        StepVerifier.create(redisAdapter.save(documentNumber, pin, notificationData))
                .assertNext(Assertions::assertNotNull)
                .verifyComplete();
    }

    @Test
    void shouldReturnDomainErrorWhenRedisDoesNotSavePin() throws Exception {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        String numberDocument = "12345678";
        String pin = "123456";
        NotificationData notificationData = NotificationData.builder()
                .phoneNumber("+573001234567")
                .pin(pin)
                .build();

        when(objectMapper.writeValueAsString(any())).thenReturn("{}");
        when(valueOperations.set(anyString(), anyString(), any(Duration.class)))
                .thenReturn(Mono.just(false));

        StepVerifier.create(redisAdapter.save(numberDocument, pin, notificationData))
                .expectErrorMatches(error ->
                        error instanceof IllegalStateException &&
                                error.getMessage().equals("No se pudo almacenar el PIN en Redis para +573001234567"))
                .verify();
    }

    @Test
    void shouldReturnDomainErrorWhenSerializationFails() throws Exception {
        String numberDocument = "12345678";
        String pin = "123456";
        NotificationData notificationData = NotificationData.builder()
                .phoneNumber("+573001234567")
                .pin(pin)
                .build();

        NotificationRedisValue redisValue = NotificationRedisValue.builder()
                .phoneNumber("+573001234567")
                .pin(pin)
                .build();

        JsonProcessingException exception =
                new JsonProcessingException("Error de serialización") {};

        when(objectMapper.writeValueAsString(any())).thenThrow(exception);

        when(redisRequestMapper.toInsert(any())).thenReturn(redisValue);

        StepVerifier.create(redisAdapter.save(numberDocument, pin, notificationData))
                .expectErrorMatches(error ->
                        error instanceof IllegalStateException &&
                                error.getMessage().equals("Error serializando el PIN"))
                .verify();
    }
}
