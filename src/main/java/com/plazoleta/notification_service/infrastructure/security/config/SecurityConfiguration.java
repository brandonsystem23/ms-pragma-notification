package com.plazoleta.notification_service.infrastructure.security.config;

import com.plazoleta.notification_service.infrastructure.security.handler.JsonAccessDeniedHandler;
import com.plazoleta.notification_service.infrastructure.security.handler.JsonAuthenticationEntryPoint;
import com.plazoleta.notification_service.infrastructure.security.jwt.JwtProvider;
import com.plazoleta.notification_service.infrastructure.security.session.BearerTokenAuthenticationConverter;
import com.plazoleta.notification_service.infrastructure.security.session.SessionAuthenticationManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationEntryPointFailureHandler;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtProvider jwtProvider;
    private final JsonAuthenticationEntryPoint jsonAuthenticationEntryPoint;
    private final JsonAccessDeniedHandler jsonAccessDeniedHandler;

    @Bean
    SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {

        AuthenticationWebFilter authenticationWebFilter =
                new AuthenticationWebFilter(
                        new SessionAuthenticationManager(jwtProvider)
                );

        authenticationWebFilter.setServerAuthenticationConverter(
                new BearerTokenAuthenticationConverter()
        );

        authenticationWebFilter.setRequiresAuthenticationMatcher(
                ServerWebExchangeMatchers.pathMatchers("/api/**")
        );

        authenticationWebFilter.setAuthenticationFailureHandler(
                new ServerAuthenticationEntryPointFailureHandler(jsonAuthenticationEntryPoint)
        );

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .exceptionHandling(spec -> spec
                        .authenticationEntryPoint(jsonAuthenticationEntryPoint)
                        .accessDeniedHandler(jsonAccessDeniedHandler)
                )
                .authorizeExchange(exchange -> exchange
                        .pathMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()
                        .pathMatchers("/api/v1/notifications/send").hasRole("EMPLEADO")
                        .anyExchange().authenticated()
                )
                .addFilterAt(authenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }
}
