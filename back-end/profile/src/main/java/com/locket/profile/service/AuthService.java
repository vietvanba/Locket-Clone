package com.locket.profile.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.locket.profile.payload.*;

public interface AuthService {
    LoginResponse signIn(LoginRequest request);

    UserResponse signUp(UserRequest request) throws JsonProcessingException;

    EmailVerificationResponse verifyEmail(String userId, String token);
}
