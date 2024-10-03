package com.locket.profile.payload;

public record ForgotPasswordResponse(String status, String email, String message) {
}
