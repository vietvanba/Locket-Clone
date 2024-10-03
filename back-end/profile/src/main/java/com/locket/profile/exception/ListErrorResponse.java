package com.locket.profile.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Data
public class ListErrorResponse {
    private HttpStatus status;
    private Long timestamp;
    private Map<String, String> errors;

    public ListErrorResponse(HttpStatus status, Map<String, String> errors) {
        this.status = status;
        this.errors = errors;
        this.timestamp = System.currentTimeMillis();
    }
}
