package com.locket.profile.service;

import com.locket.profile.payload.ProfileResponse;

import java.util.List;

public interface ProfileService {
    List<ProfileResponse> getAllUser();
}
