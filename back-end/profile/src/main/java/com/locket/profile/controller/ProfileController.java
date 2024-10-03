package com.locket.profile.controller;

import com.locket.profile.payload.ChangePasswordRequest;
import com.locket.profile.payload.ProfileResponse;
import com.locket.profile.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService service;

    @GetMapping
    public ResponseEntity<List<ProfileResponse>> getAllProfile() {
        return ResponseEntity.ok(service.getAllUser());
    }

    @PostMapping("/forgot-password/{email}")
    public ResponseEntity<?> forgotPassword(@PathVariable(name = "email") String email) {
        return ResponseEntity.ok(service.forgotPassword(email));
    }

    @PostMapping("/change-password/{userId}")
    public ResponseEntity<?> changePassword(@PathVariable(name = "userId") String userId, @Valid @RequestBody ChangePasswordRequest request) {
        return ResponseEntity.ok(service.changePassword(userId, request));
    }
}
