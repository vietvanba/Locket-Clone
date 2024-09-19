package com.locket.profile.client;

import com.locket.profile.payload.TokenDetails;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(value = "keycloak", url = "${keycloak.serverUrl}")
public interface KeycloakClient {
    @PostMapping(value = "/realms/${keycloak.realm}/protocol/openid-connect/token/introspect", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    ResponseEntity<TokenDetails> introspectToken(
            @RequestBody Map<String, ?> body);
}
