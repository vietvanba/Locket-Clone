package com.locket.gateway.config;

import com.locket.gateway.converter.CustomJwtAuthenticationConverter;
import com.locket.gateway.converter.ReactiveKeycloakRoleConverter;
import com.locket.gateway.exception.DelegatedAccessDeniedHandler;
import com.locket.gateway.exception.DelegatedAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    @Autowired
    DelegatedAuthenticationEntryPoint authenticationEntryPoint;
    @Autowired
    DelegatedAccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        CustomJwtAuthenticationConverter customConverter = new CustomJwtAuthenticationConverter(new ReactiveKeycloakRoleConverter());
        return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange.pathMatchers("/api/auth/**")
                        .permitAll()
                        .pathMatchers("/api/profile")
                        .hasRole("ADMIN")
                        .pathMatchers("/api/profile/{id}")
                        .access(this::checkProfileAccess)
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth ->
                        oauth.authenticationEntryPoint(authenticationEntryPoint)
                                .accessDeniedHandler(accessDeniedHandler)
                                .jwt(jwt -> jwt
                                        .jwtAuthenticationConverter(new ReactiveJwtAuthenticationConverterAdapter(customConverter.getJwtAuthenticationConverter()))))
                .build();
    }

    private Mono<AuthorizationDecision> checkProfileAccess(Mono<Authentication> authentication, AuthorizationContext context) {
        return authentication
                .map(auth -> {
                    String userId = extractUserIdFromToken(auth);
                    String pathId = (String) context.getVariables().get("id");
                    return userId.equals(pathId);
                })
                .map(AuthorizationDecision::new);
    }

    private String extractUserIdFromToken(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        return jwt.getClaim("sub");
    }
}
