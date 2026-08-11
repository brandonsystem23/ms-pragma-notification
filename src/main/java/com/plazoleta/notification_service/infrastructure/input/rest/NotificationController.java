package com.plazoleta.notification_service.infrastructure.input.rest;

import com.plazoleta.notification_service.application.dto.request.SendNotificationRequest;
import com.plazoleta.notification_service.application.dto.response.NotificationResponse;
import com.plazoleta.notification_service.application.service.NotificationApplicationService;
import com.plazoleta.notification_service.infrastructure.util.BearerTokenExtractor;
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

    private final NotificationApplicationService notificationApplicationService;

    @PostMapping("/send")
    @Operation(summary = "Enviar PIN", description = "Envia PIN por WhatsApp. Requiere rol EMPLEADO")
    public Mono<NotificationResponse> sendNotification(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @Valid @RequestBody SendNotificationRequest request
    ) {
        String token = BearerTokenExtractor.extract(authorizationHeader);
        return notificationApplicationService.sendNotification(token, request);
    }
}
