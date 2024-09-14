package com.locket.profile.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.locket.profile.payload.ProfileResponse;
import com.locket.profile.payload.UserRequest;
import com.locket.profile.payload.UserResponse;

import java.util.List;

public interface ProfileService {
    UserResponse createUser(UserRequest request) throws JsonProcessingException;

    List<ProfileResponse> getAllUser();
}
