package com.plazoleta.notification_service.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SendNotificationRequest(

        @NotBlank(message = "El phone es obligatorio")
        @Size(
                max = 13,
                message = "El phone no puede tener más de 13 caracteres"
        )
        @Pattern(
                regexp = "^\\+?\\d+$",
                message = "El phone solo puede contener números y opcionalmente iniciar con +"
        )
        String phone
) {
}
