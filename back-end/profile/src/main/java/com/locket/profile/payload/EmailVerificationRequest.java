package com.locket.profile.payload;

import lombok.Data;

@Data
public class EmailVerificationRequest {
    private String userId;
    private String token;
}
