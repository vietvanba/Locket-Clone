package com.locket.profile.service;

import com.locket.profile.payload.ChangePasswordRequest;
import com.locket.profile.payload.ChangePasswordResponse;
import com.locket.profile.payload.ForgotPasswordResponse;
import com.locket.profile.payload.ProfileResponse;

import java.util.List;

public interface ProfileService {
    List<ProfileResponse> getAllUser();

    ForgotPasswordResponse forgotPassword(String email);

    ChangePasswordResponse changePassword(String userId, ChangePasswordRequest request);

}
