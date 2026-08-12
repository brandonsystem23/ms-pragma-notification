package com.plazoleta.notification_service.infrastructure.input.rest;

import com.plazoleta.notification_service.domain.exception.InvalidPinException;
import com.plazoleta.notification_service.domain.exception.InvalidTokenException;
import com.plazoleta.notification_service.domain.exception.PinStorageException;
import com.plazoleta.notification_service.domain.exception.UnauthorizedRoleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private ServerWebExchange exchange;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/v1/notifications/send").build()
        );
    }

    @Test
    void shouldHandleInvalidToken() {
        ResponseEntity<ErrorResponse> responseEntity =
                handler.handleInvalidToken(new InvalidTokenException(), exchange);

        ErrorResponse response = getBody(responseEntity);

        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.status());
        assertEquals("Unauthorized", response.error());
        assertEquals("Token inválido o expirado", response.message());
        assertEquals("/api/v1/notifications/send", response.path());
        assertNotNull(response.timestamp());
        assertNotNull(response.details());
        assertTrue(response.details().isEmpty());
    }

    @Test
    void shouldHandleUnauthorizedRole() {
        ResponseEntity<ErrorResponse> responseEntity =
                handler.handleUnauthorizedRole(new UnauthorizedRoleException(), exchange);

        ErrorResponse response = getBody(responseEntity);

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        assertEquals(HttpStatus.FORBIDDEN.value(), response.status());
        assertEquals("Forbidden", response.error());
        assertEquals("Solo un EMPLEADO puede enviar el PIN", response.message());
        assertEquals("/api/v1/notifications/send", response.path());
        assertNotNull(response.timestamp());
        assertNotNull(response.details());
        assertTrue(response.details().isEmpty());
    }

    @Test
    void shouldHandlePinStorageError() {
        ResponseEntity<ErrorResponse> responseEntity =
                handler.handlePinStorageError(new PinStorageException(), exchange);

        ErrorResponse response = getBody(responseEntity);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, responseEntity.getStatusCode());
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE.value(), response.status());
        assertEquals("Service Unavailable", response.error());
        assertEquals("No se pudo almacenar el PIN", response.message());
        assertEquals("/api/v1/notifications/send", response.path());
        assertNotNull(response.timestamp());
        assertNotNull(response.details());
        assertTrue(response.details().isEmpty());
    }

    @Test
    void shouldHandleInvalidPinError() {
        ResponseEntity<ErrorResponse> responseEntity =
                handler.handleInvalidPinError(new InvalidPinException(4, 6), exchange);

        ErrorResponse response = getBody(responseEntity);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.status());
        assertEquals("Bad Request", response.error());
        assertEquals("La longitud del PIN dede ser entre 4 y 6", response.message());
        assertEquals("/api/v1/notifications/send", response.path());
        assertNotNull(response.timestamp());
        assertNotNull(response.details());
        assertTrue(response.details().isEmpty());
    }

    @Test
    void shouldHandleIllegalArgument() {
        ResponseEntity<ErrorResponse> responseEntity =
                handler.handleIllegalArgument(
                        new IllegalArgumentException("Authorization header inválido"),
                        exchange
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
                handler.handleGeneric(
                        new RuntimeException("Error inesperado"),
                        exchange
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
    void shouldHandleValidationErrors() throws Exception {
        Object target = new TestRequest();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "testRequest");
        bindingResult.addError(new FieldError("testRequest", "phone", "El phone es obligatorio"));
        bindingResult.addError(new FieldError("testRequest", "phone", "El phone solo puede contener números y opcionalmente iniciar con +"));

        var method = TestController.class.getDeclaredMethod("testMethod", TestRequest.class);

        WebExchangeBindException exception = new WebExchangeBindException(
                new org.springframework.core.MethodParameter(method, 0),
                bindingResult
        );

        ResponseEntity<ErrorResponse> responseEntity =
                handler.handleValidationErrors(exception, exchange);

        ErrorResponse response = getBody(responseEntity);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.status());
        assertEquals("Bad Request", response.error());
        assertEquals("Error de validación", response.message());
        assertEquals("/api/v1/notifications/send", response.path());
        assertNotNull(response.timestamp());
        assertNotNull(response.details());
        assertEquals(2, response.details().size());
        assertTrue(response.details().contains("phone: El phone es obligatorio"));
        assertTrue(response.details().contains("phone: El phone solo puede contener números y opcionalmente iniciar con +"));
    }

    private ErrorResponse getBody(ResponseEntity<ErrorResponse> responseEntity) {
        ErrorResponse body = responseEntity.getBody();
        assertNotNull(body);
        return body;
    }

    static class TestController {
        public void testMethod(@ModelAttribute TestRequest request) {
        }
    }

    static class TestRequest {
        private String phone;

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }
    }
}
