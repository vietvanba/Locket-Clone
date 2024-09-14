package com.locket.profile.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    public String access_token;
    public Long expires_in;
    public String refresh_token;
    public Long refresh_expires_in;
    public String token_type;
    public String scope;
}
