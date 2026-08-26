package com.plazoleta.notification_service.domain.spi;

import com.plazoleta.notification_service.domain.model.AuthSession;

public interface IJwtProviderPort {

    AuthSession validateAndGetUser(String token);
}
