package com.plazoleta.notification_service.infrastructure.exceptionhandler;

import com.plazoleta.notification_service.domain.exception.DomainErrorCode;
import com.plazoleta.notification_service.domain.exception.DomainException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;
    private ServerWebExchange serverWebExchange;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
        serverWebExchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/v1/notifications/send").build()
        );
    }

    @Test
    void shouldHandleValidationDomainException() {
        ResponseEntity<ErrorResponse> responseEntity =
                globalExceptionHandler.handleDomainException(
                        new DomainException(
                                DomainErrorCode.VALIDATION_ERROR,
                                "El phone es obligatorio"
                        ),
                        serverWebExchange
                );

        ErrorResponse response = getBody(responseEntity);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.status());
        assertEquals("Bad Request", response.error());
        assertEquals("El phone es obligatorio", response.message());
        assertEquals("/api/v1/notifications/send", response.path());
        assertNotNull(response.timestamp());
        assertEquals(List.of(), response.details());
    }

    @Test
    void shouldHandleInvalidTokenDomainException() {
        ResponseEntity<ErrorResponse> responseEntity =
                globalExceptionHandler.handleDomainException(
                        new DomainException(
                                DomainErrorCode.INVALID_TOKEN,
                                "Token inválido o expirado"
                        ),
                        serverWebExchange
                );

        ErrorResponse response = getBody(responseEntity);

        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.status());
        assertEquals("Unauthorized", response.error());
        assertEquals("Token inválido o expirado", response.message());
    }

    @Test
    void shouldHandleAccessDeniedDomainException() {
        ResponseEntity<ErrorResponse> responseEntity =
                globalExceptionHandler.handleDomainException(
                        new DomainException(
                                DomainErrorCode.ACCESS_DENIED,
                                "Solo un EMPLEADO puede enviar el PIN"
                        ),
                        serverWebExchange
                );

        ErrorResponse response = getBody(responseEntity);

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        assertEquals(HttpStatus.FORBIDDEN.value(), response.status());
        assertEquals("Forbidden", response.error());
        assertEquals("Solo un EMPLEADO puede enviar el PIN", response.message());
    }

    @Test
    void shouldHandleStorageErrorDomainException() {
        ResponseEntity<ErrorResponse> responseEntity =
                globalExceptionHandler.handleDomainException(
                        new DomainException(
                                DomainErrorCode.STORAGE_ERROR,
                                "No se pudo almacenar el PIN"
                        ),
                        serverWebExchange
                );

        ErrorResponse response = getBody(responseEntity);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, responseEntity.getStatusCode());
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE.value(), response.status());
        assertEquals("Service Unavailable", response.error());
        assertEquals("No se pudo almacenar el PIN", response.message());
    }

    @Test
    void shouldHandleExternalServiceDomainException() {
        ResponseEntity<ErrorResponse> responseEntity =
                globalExceptionHandler.handleDomainException(
                        new DomainException(
                                DomainErrorCode.EXTERNAL_SERVICE_ERROR,
                                "No fue posible enviar la notificación"
                        ),
                        serverWebExchange
                );

        ErrorResponse response = getBody(responseEntity);

        assertEquals(HttpStatus.BAD_GATEWAY, responseEntity.getStatusCode());
        assertEquals(HttpStatus.BAD_GATEWAY.value(), response.status());
        assertEquals("Bad Gateway", response.error());
        assertEquals("No fue posible enviar la notificación", response.message());
    }

    @Test
    void shouldHandleInternalErrorDomainException() {
        ResponseEntity<ErrorResponse> responseEntity =
                globalExceptionHandler.handleDomainException(
                        new DomainException(
                                DomainErrorCode.INTERNAL_ERROR,
                                "Ocurrió un error interno en el servidor"
                        ),
                        serverWebExchange
                );

        ErrorResponse response = getBody(responseEntity);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.status());
        assertEquals("Internal Server Error", response.error());
        assertEquals("Ocurrió un error interno en el servidor", response.message());
    }

    @Test
    void shouldHandleIllegalArgument() {
        ResponseEntity<ErrorResponse> responseEntity =
                globalExceptionHandler.handleIllegalArgument(
                        new IllegalArgumentException("Authorization header inválido"),
                        serverWebExchange
                );

        ErrorResponse response = getBody(responseEntity);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.status());
        assertEquals("Bad Request", response.error());
        assertEquals("Authorization header inválido", response.message());
        assertEquals("/api/v1/notifications/send", response.path());
        assertNotNull(response.timestamp());
        assertNotNull(response.details());
        assertTrue(response.details().isEmpty());
    }

    @Test
    void shouldHandleGenericException() {
        ResponseEntity<ErrorResponse> responseEntity =
                globalExceptionHandler.handleGeneric(
                        new RuntimeException("Error inesperado"),
                        serverWebExchange
                );

        ErrorResponse response = getBody(responseEntity);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.status());
        assertEquals("Internal Server Error", response.error());
        assertEquals("Ocurrió un error interno en el servidor", response.message());
        assertEquals("/api/v1/notifications/send", response.path());
        assertNotNull(response.timestamp());
        assertNotNull(response.details());
        assertTrue(response.details().isEmpty());
    }

    @Test
    void shouldHandleValidationErrors() {
        WebExchangeBindException exception = mock(WebExchangeBindException.class);

        FieldError phoneRequiredError = new FieldError(
                "testRequest",
                "phone",
                "El phone es obligatorio"
        );

        FieldError phoneFormatError = new FieldError(
                "testRequest",
                "phone",
                "El phone solo puede contener números y opcionalmente iniciar con +"
        );

        when(exception.getFieldErrors())
                .thenReturn(List.of(phoneRequiredError, phoneFormatError));

        ResponseEntity<ErrorResponse> responseEntity =
                globalExceptionHandler.handleValidationErrors(exception, serverWebExchange);

        ErrorResponse response = getBody(responseEntity);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.status());
        assertEquals("Bad Request", response.error());
        assertEquals("Error de validación", response.message());
        assertEquals("/api/v1/notifications/send", response.path());
        assertNotNull(response.timestamp());

        assertEquals(
                List.of(
                        "phone: El phone es obligatorio",
                        "phone: El phone solo puede contener números y opcionalmente iniciar con +"
                ),
                response.details()
        );
    }

    private ErrorResponse getBody(ResponseEntity<ErrorResponse> responseEntity) {
        ErrorResponse body = responseEntity.getBody();
        assertNotNull(body);
        return body;
    }
}
