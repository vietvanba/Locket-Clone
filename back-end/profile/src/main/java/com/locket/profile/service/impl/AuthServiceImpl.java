package com.locket.profile.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.locket.profile.config.KeycloakConfig;
import com.locket.profile.constant.KafkaTopic;
import com.locket.profile.exception.RegistrationException;
import com.locket.profile.payload.*;
import com.locket.profile.producer.KafkaProducer;
import com.locket.profile.service.AuthService;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final KeycloakConfig keycloakConfig;
    private final UsersResource usersResource;
    private final KafkaProducer producer;

    @Override
    public LoginResponse signIn(LoginRequest request) {
        Keycloak keycloak = keycloakConfig.getInstanceUser(request.getUsername(), request.getPassword());
        try {
            AccessTokenResponse token = keycloak.tokenManager().getAccessToken();
            return new LoginResponse(
                    token.getToken(),
                    token.getExpiresIn(),
                    token.getRefreshToken(),
                    token.getRefreshExpiresIn(),
                    token.getTokenType(),
                    token.getScope()
            );
        } catch (Exception e) {
            throw new RegistrationException("Username or password is incorrect", HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    public UserResponse signUp(UserRequest request) throws JsonProcessingException {
        UserRepresentation userRepresentation = getUserRepresentation(request);
        Response response = usersResource.create(userRepresentation);
        return switch (response.getStatus()) {
            case 201 -> {
                log.info("New user created");
                String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
                producer.send(KafkaTopic.EMAIL_SENDER_TOPIC,
                        EmailKey.builder()
                                .email(userRepresentation.getEmail())
                                .userId(userId)
                                .build(),
                        EmailValue.builder()
                                .email(userRepresentation.getEmail())
                                .userId(userId)
                                .token("Token A")
                                .type("registry")
                                .build());
                yield new UserResponse(userId, request.getUsername(), request.getEmail());
            }
            default -> {
                String errorMessage = getErrorMessage(response.readEntity(String.class));
                log.info(errorMessage);
                log.info(request.toString());
                throw new RegistrationException(errorMessage, HttpStatus.valueOf(response.getStatus()));
            }
        };
    }

    private static UserRepresentation getUserRepresentation(UserRequest request) {
        CredentialRepresentation password = new CredentialRepresentation();
        password.setType(CredentialRepresentation.PASSWORD);
        password.setValue(request.getPassword());

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(request.getUsername());
        userRepresentation.setEmail(request.getEmail());
        userRepresentation.setFirstName(request.getFirstName());
        userRepresentation.setLastName(request.getLastName());
        userRepresentation.setEmailVerified(true);
        userRepresentation.setEnabled(true);
        userRepresentation.setCredentials(List.of(password));
        return userRepresentation;
    }

    private String getErrorMessage(String error) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(error);
        String errorMessage = jsonNode.path("errorMessage").asText();
        return errorMessage.isEmpty() ? "An unknown error occurred" : errorMessage;
    }
}
