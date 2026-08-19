package com.plazoleta.notification_service.infrastructure.input.rest;

import com.plazoleta.notification_service.domain.exception.DomainErrorCode;
import com.plazoleta.notification_service.domain.exception.DomainException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomainException(DomainException ex, ServerWebExchange exchange) {
        HttpStatus status = mapStatus(ex.getCode());
        return buildResponse(status, ex.getMessage(), exchange, List.of());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex, ServerWebExchange exchange) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), exchange, List.of());
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(WebExchangeBindException ex, ServerWebExchange exchange) {
        List<String> details = ex.getFieldErrors()
                .stream()
                .map(this::formatFieldError)
                .toList();

        return buildResponse(HttpStatus.BAD_REQUEST, "Error de validación", exchange, details);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, ServerWebExchange exchange) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error interno en el servidor", exchange, List.of());
    }

    private HttpStatus mapStatus(DomainErrorCode code) {
        return switch (code) {
            case VALIDATION_ERROR -> HttpStatus.BAD_REQUEST;
            case INVALID_TOKEN -> HttpStatus.UNAUTHORIZED;
            case ACCESS_DENIED -> HttpStatus.FORBIDDEN;
            case STORAGE_ERROR -> HttpStatus.SERVICE_UNAVAILABLE;
            case EXTERNAL_SERVICE_ERROR -> HttpStatus.BAD_GATEWAY;
            case INTERNAL_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }

    private ResponseEntity<ErrorResponse> buildResponse(
            HttpStatus status,
            String message,
            ServerWebExchange exchange,
            List<String> details
    ) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now(ZoneId.of("America/Lima")))
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(exchange.getRequest().getPath().value())
                .details(details)
                .build();

        return ResponseEntity.status(status).body(errorResponse);
    }

    private String formatFieldError(FieldError fieldError) {
        return fieldError.getField() + ": " + fieldError.getDefaultMessage();
    }
}
