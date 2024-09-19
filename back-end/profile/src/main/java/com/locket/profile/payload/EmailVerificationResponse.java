package com.locket.profile.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailVerificationResponse {
    private HttpStatus status;
    private String detail;
}
