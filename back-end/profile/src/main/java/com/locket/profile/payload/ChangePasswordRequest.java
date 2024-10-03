package com.locket.profile.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangePasswordRequest {
    @NotNull(message = "The type of change password request must not null")
    private Boolean isForgotPassword;
    private String oldPassword;
    @NotBlank(message = "The new password must be filled")
    private String newPassword;
    private String verificationCode;
}
