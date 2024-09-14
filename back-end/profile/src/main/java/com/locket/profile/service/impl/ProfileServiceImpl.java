package com.locket.profile.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.locket.profile.exception.RegistrationException;
import com.locket.profile.payload.ProfileResponse;
import com.locket.profile.payload.UserRequest;
import com.locket.profile.payload.UserResponse;
import com.locket.profile.service.ProfileService;
import jakarta.annotation.PostConstruct;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    private final Keycloak keycloak;
    private final ModelMapper modelMapper;
    private UsersResource usersResource;
    @Value("${keycloak.realm}")
    private String realm;

    @PostConstruct
    private void init() {
        usersResource = keycloak.realm(realm).users();
    }

    @Override
    public UserResponse createUser(UserRequest request) throws JsonProcessingException {
        UserRepresentation userRepresentation = getUserRepresentation(request);
        Response response = usersResource.create(userRepresentation);
        return switch (response.getStatus()) {
            case 201 -> {
                log.info("New user created");
                String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
                yield new UserResponse(userId, request.getUsername(), request.getEmail());
            }
            default -> {
                String errorMessage = getErrorMessage(response.readEntity(String.class));
                log.info(errorMessage);
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

    @Override
    public List<ProfileResponse> getAllUser() {
        List<ProfileResponse> list = new ArrayList<>();
        log.info("Get all users");
        usersResource.list().stream().map(x ->
                modelMapper.map(x, ProfileResponse.class)
        ).forEach(list::add);
        return list;
    }

    private String getErrorMessage(String error) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(error);
        String errorMessage = jsonNode.path("errorMessage").asText();
        return errorMessage.isEmpty() ? "An unknown error occurred" : errorMessage;
    }
}
