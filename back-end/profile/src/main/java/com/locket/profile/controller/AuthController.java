package com.locket.profile.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.locket.profile.payload.LoginRequest;
import com.locket.profile.payload.LoginResponse;
import com.locket.profile.payload.UserRequest;
import com.locket.profile.payload.UserResponse;
import com.locket.profile.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
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
}
