package com.locket.profile.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.locket.profile.payload.*;
import com.locket.profile.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(value = "*")
public class AuthController {
    private final AuthService service;

    @PostMapping("/signIn")
    public ResponseEntity<LoginResponse> signIn(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(service.signIn(request));
    }

    @PostMapping("/signUp")
    public ResponseEntity<UserResponse> signUp(@RequestBody UserRequest request) throws JsonProcessingException {
        return ResponseEntity.ok(service.signUp(request));
    }

    @GetMapping("/verify-email")
    public ResponseEntity<EmailVerificationResponse> verifyEmail(@RequestBody EmailVerificationRequest request) {
        return ResponseEntity.ok(service.verifyEmail(request.getUserId(), request.getToken()));
    }
}
