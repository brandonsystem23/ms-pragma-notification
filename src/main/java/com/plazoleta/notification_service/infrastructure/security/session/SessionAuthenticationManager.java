package com.plazoleta.notification_service.infrastructure.security.session;

import com.plazoleta.notification_service.domain.spi.IJwtProviderPort;
import com.plazoleta.notification_service.infrastructure.out.jwt.dto.AuthenticatedUser;
import com.plazoleta.notification_service.infrastructure.out.jwt.mapper.AuthMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class SessionAuthenticationManager implements ReactiveAuthenticationManager {

    private final IJwtProviderPort iJwtProviderPort;
    private final AuthMapper authMapper;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.fromCallable(() -> {
            try {
                String token = authentication.getCredentials().toString();
                AuthenticatedUser session = authMapper.toDto(iJwtProviderPort.validateAndGetUser(token));
                return buildAuthentication(session);
            } catch (Exception ex) {
                throw new BadCredentialsException("Token inválido o expirado", ex);
            }
        });
    }

    private Authentication buildAuthentication(AuthenticatedUser authenticatedUser) {
        return new UsernamePasswordAuthenticationToken(
                authenticatedUser,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + authenticatedUser.role()))
        );
    }
}
