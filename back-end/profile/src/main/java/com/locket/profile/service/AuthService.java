package com.locket.profile.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.locket.profile.payload.LoginRequest;
import com.locket.profile.payload.LoginResponse;
import com.locket.profile.payload.UserRequest;
import com.locket.profile.payload.UserResponse;

public interface AuthService {
    LoginResponse signIn(LoginRequest request);

    UserResponse signUp(UserRequest request) throws JsonProcessingException;
}
