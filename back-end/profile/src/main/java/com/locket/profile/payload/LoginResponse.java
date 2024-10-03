package com.locket.profile.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String access_token;
    private Long expires_in;
    private String refresh_token;
    private Long refresh_expires_in;
    private String token_type;
    private String scope;
    private String userId;
    private String username;
}
