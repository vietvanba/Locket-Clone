package com.locket.profile.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.locket.profile.model.ProfileResponse;
import com.locket.profile.model.UserRequest;
import com.locket.profile.model.UserResponse;

import java.util.List;

public interface ProfileService {
    UserResponse createUser(UserRequest request) throws JsonProcessingException;

    List<ProfileResponse> getAllUser();
}
