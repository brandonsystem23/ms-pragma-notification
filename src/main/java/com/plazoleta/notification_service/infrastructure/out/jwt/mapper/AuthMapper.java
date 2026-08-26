package com.plazoleta.notification_service.infrastructure.out.jwt.mapper;

import com.plazoleta.notification_service.domain.model.AuthSession;
import com.plazoleta.notification_service.infrastructure.out.jwt.dto.AuthenticatedUser;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthMapper {

    AuthenticatedUser toDto(AuthSession authSession);
}
