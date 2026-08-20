package com.plazoleta.notification_service.infrastructure.input.rest;

import com.plazoleta.notification_service.application.dto.request.SendNotificationRequest;
import com.plazoleta.notification_service.application.dto.response.NotificationResponse;
import com.plazoleta.notification_service.application.handler.INotificationHandler;
import com.plazoleta.notification_service.infrastructure.util.Utils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notificación", description = "Endpoint para envio de PIN")
public class NotificationController {

    private final INotificationHandler iNotificationHandler;

    @PostMapping("/send")
    @Operation(summary = "Enviar PIN", description = "Envía PIN por Vonage. Requiere rol EMPLEADO")
    public Mono<NotificationResponse> sendNotification(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @Valid @RequestBody SendNotificationRequest request
    ) {
        String token = Utils.extract(authorizationHeader);
        return iNotificationHandler.sendNotification(token, request);
    }
}
