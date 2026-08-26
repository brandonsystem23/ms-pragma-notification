package com.plazoleta.notification_service.infrastructure.input.rest;

import com.plazoleta.notification_service.application.dto.request.SendNotificationRequest;
import com.plazoleta.notification_service.application.dto.response.NotificationResponse;
import com.plazoleta.notification_service.application.handler.INotificationHandler;
import com.plazoleta.notification_service.infrastructure.out.jwt.dto.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Notificación", description = "Endpoint para envio de PIN")
public class NotificationController {

    private final INotificationHandler iNotificationHandler;

    @PostMapping("/send")
    @Operation(summary = "Enviar PIN", description = "Envía PIN por Vonage. Requiere rol EMPLEADO")
    public Mono<NotificationResponse> sendNotification(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @Valid @RequestBody SendNotificationRequest request
    ) {
        log.info("Solicitud de envio de notificacion SMS a {}", request.phoneNumber());
        return iNotificationHandler.sendNotification(request, authenticatedUser.numberDocument());
    }
}
