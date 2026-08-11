package com.plazoleta.notification_service.infrastructure.input.rest;

import com.plazoleta.notification_service.application.dto.request.SendNotificationRequest;
import com.plazoleta.notification_service.application.dto.response.NotificationResponse;
import com.plazoleta.notification_service.application.service.NotificationApplicationService;
import com.plazoleta.notification_service.infrastructure.security.session.BearerTokenExtractor;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationApplicationService notificationApplicationService;

    @PostMapping("/send")
    public Mono<NotificationResponse> sendNotification(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @Valid @RequestBody SendNotificationRequest request
    ) {
        String token = BearerTokenExtractor.extract(authorizationHeader);
        return notificationApplicationService.sendNotification(token, request);
    }
}
