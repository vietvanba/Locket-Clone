package com.locket.profile.service.impl;

import com.locket.profile.config.KeycloakConfig;
import com.locket.profile.constant.KafkaTopic;
import com.locket.profile.exception.NotFoundResourceException;
import com.locket.profile.payload.*;
import com.locket.profile.producer.KafkaProducer;
import com.locket.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.modelmapper.ModelMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.locket.profile.constant.EmailType.FORGOT_PASSWORD;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    private final ModelMapper modelMapper;
    private final UsersResource usersResource;
    private final KafkaProducer producer;
    private final RedisTemplate<String, String> redisTemplate;
    private final KeycloakConfig keycloakConfig;

    @Override
    public List<ProfileResponse> getAllUser() {
        List<ProfileResponse> list = new ArrayList<>();
        log.info("Get all users");
        usersResource.list().stream().map(x ->
                modelMapper.map(x, ProfileResponse.class)
        ).forEach(list::add);
        return list;
    }

    @Override
    public ForgotPasswordResponse forgotPassword(String email) {
        List<UserRepresentation> listUser = usersResource.searchByEmail(email, true);
        if (listUser.isEmpty()) {
            log.info("User with email {} not found", email);
            throw new NotFoundResourceException("User with email " + email + " not found", HttpStatus.NOT_FOUND);
        }
        UserRepresentation userRepresentation = listUser.get(0);
        String verificationCode = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(userRepresentation.getId(), verificationCode);
        producer.send(KafkaTopic.EMAIL_SENDER_TOPIC,
                EmailKey.builder()
                        .email(userRepresentation.getEmail())
                        .userId(userRepresentation.getId())
                        .build(),
                EmailValue.builder()
                        .name(userRepresentation.getFirstName())
                        .email(userRepresentation.getEmail())
                        .token(verificationCode)
                        .userId(userRepresentation.getId())
                        .type(FORGOT_PASSWORD)
                        .build()
        );
        return new ForgotPasswordResponse("SUCCESS", userRepresentation.getEmail(), "Please check your email");
    }

    @Override
    public ChangePasswordResponse changePassword(String userId, ChangePasswordRequest request) {
        UserResource userResource = usersResource.get(userId);
        if (request.getIsForgotPassword()) {
            if (request.getVerificationCode() == null || request.getVerificationCode().isEmpty())
                return new ChangePasswordResponse("false", "The verification code is null", userId);

            String verificationCode = redisTemplate.opsForValue().get(userId);
            if (verificationCode == null)
                return new ChangePasswordResponse("false", "The verification is not found. Please try again or contact the Administrator", userId);

            if (!verificationCode.equals(request.getVerificationCode()))
                return new ChangePasswordResponse("false", "The verification code is not match", userId);
            return changePasswordAction(userResource, userId, request);
        } else {
            if (request.getVerificationCode() == null || request.getOldPassword().isEmpty())
                return new ChangePasswordResponse("false", "The old password is null", userId);
            Keycloak keycloak = keycloakConfig.getInstanceUser(userResource.toRepresentation().getEmail(), request.getOldPassword());
            try {
                keycloak.tokenManager().getAccessToken();
                return changePasswordAction(userResource, userId, request);
            } catch (Exception e) {
                throw new NotFoundResourceException("The old password is not found", HttpStatus.NOT_FOUND);
            }
        }
    }

    private ChangePasswordResponse changePasswordAction(UserResource userResource, String userId, ChangePasswordRequest request) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(request.getNewPassword());
        userResource.resetPassword(credential);
        return new ChangePasswordResponse("true", "The password has been changed", userId);
    }
}
