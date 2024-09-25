package com.locket.gateway.converter;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.Collection;

public class CustomJwtAuthenticationConverter {

    private final ReactiveKeycloakRoleConverter keycloakRoleConverter;

    public CustomJwtAuthenticationConverter(ReactiveKeycloakRoleConverter keycloakRoleConverter) {
        this.keycloakRoleConverter = keycloakRoleConverter;
    }

    public JwtAuthenticationConverter getJwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Collection<GrantedAuthority> keycloakAuthorities = keycloakRoleConverter.convert(jwt);
            JwtGrantedAuthoritiesConverter defaultAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
            Collection<GrantedAuthority> defaultAuthorities = defaultAuthoritiesConverter.convert(jwt);
            keycloakAuthorities.addAll(defaultAuthorities);
            return keycloakAuthorities;
        });
        return converter;
    }
}
