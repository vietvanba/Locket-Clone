package com.locket.profile.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.locket.profile.client.KeycloakClient;
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
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.locket.profile.constant.EmailType.VERIFY_EMAIL;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final KeycloakConfig keycloakConfig;
    private final UsersResource usersResource;
    private final KafkaProducer producer;
    private final KeycloakClient client;

    @Value("${keycloak.clientId}")
    private String clientId;
    @Value("${keycloak.clientSecret}")
    private String clientSecret;

    @Override
    public LoginResponse signIn(LoginRequest request) {
        Keycloak keycloak = keycloakConfig.getInstanceUser(request.getUsername(), request.getPassword());
        try {
            AccessTokenResponse token = keycloak.tokenManager().getAccessToken();
            List<UserRepresentation> userRepresentation = usersResource.searchByUsername(request.getUsername(), true);
            return new LoginResponse(
                    token.getToken(),
                    token.getExpiresIn(),
                    token.getRefreshToken(),
                    token.getRefreshExpiresIn(),
                    token.getTokenType(),
                    token.getScope(),
                    userRepresentation.get(0).getId(),
                    userRepresentation.get(0).getUsername()
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
                Keycloak keycloak = keycloakConfig.getInstanceUser(request.getUsername(), request.getPassword());
                String token = keycloak.tokenManager().getAccessToken().getToken();
                String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
                producer.send(KafkaTopic.EMAIL_SENDER_TOPIC,
                        EmailKey.builder()
                                .email(userRepresentation.getEmail())
                                .userId(userId)
                                .build(),
                        EmailValue.builder()
                                .email(userRepresentation.getEmail())
                                .userId(userId)
                                .name(userRepresentation.getFirstName() + " " + userRepresentation.getLastName())
                                .token(token)
                                .type(VERIFY_EMAIL)
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

    @Override
    public EmailVerificationResponse verifyEmail(String userId, String token) {
        Map<String, String> body = new HashMap<>();
        body.put("client_id", clientId);
        body.put("client_secret", clientSecret);
        body.put("token", token);

        try {
            ResponseEntity<?> response = client.introspectToken(body);
            if (!(response.getBody() instanceof TokenDetails tokenDetails))
                return new EmailVerificationResponse(HttpStatus.BAD_REQUEST, "Invalid token or response");
            if (!tokenDetails.getSub().equals(userId))
                throw new IllegalStateException("User ID mismatch or email already verified");
            UserResource userResource = usersResource.get(userId);
            UserRepresentation userRepresentation = userResource.toRepresentation();
            userRepresentation.setEmailVerified(true);
            userResource.update(userRepresentation);
            userResource.logout();
            return new EmailVerificationResponse(HttpStatus.OK, "Email verified");

        } catch (IllegalStateException e) {
            return new EmailVerificationResponse(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (Exception e) {
            return new EmailVerificationResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Verification failed: " + e.getMessage());
        }
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
