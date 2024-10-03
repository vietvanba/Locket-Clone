package com.locket.profile.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class NotFoundResourceException extends RuntimeException {
    private HttpStatus status;
    private Long timestamp = System.currentTimeMillis();

    public NotFoundResourceException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
