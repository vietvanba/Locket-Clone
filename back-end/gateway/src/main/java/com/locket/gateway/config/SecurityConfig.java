package com.locket.gateway.config;

import com.locket.gateway.converter.CustomJwtAuthenticationConverter;
import com.locket.gateway.converter.ReactiveKeycloakRoleConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        CustomJwtAuthenticationConverter customConverter = new CustomJwtAuthenticationConverter(new ReactiveKeycloakRoleConverter());
        return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange.pathMatchers("/api/auth/**")
                        .permitAll()
                        .pathMatchers("/api/profile")
                        .hasRole("ADMIN")
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth ->
                        oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(new ReactiveJwtAuthenticationConverterAdapter(customConverter.getJwtAuthenticationConverter()))))
                .build();
    }
}
