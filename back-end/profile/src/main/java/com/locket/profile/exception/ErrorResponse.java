package com.locket.profile.exception;

import org.springframework.http.HttpStatus;

public record ErrorResponse(HttpStatus status, String message, Long timestamp) {
}
