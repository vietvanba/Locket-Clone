package com.locket.profile.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class RegistrationException extends RuntimeException {
    private HttpStatus status;
    private Long timestamp = System.currentTimeMillis();

    public RegistrationException(String message) {
        super(message);
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public RegistrationException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
