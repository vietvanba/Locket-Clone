package com.locket.profile.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.locket.profile.model.ProfileResponse;
import com.locket.profile.model.UserRequest;
import com.locket.profile.model.UserResponse;
import com.locket.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService service;

    @PostMapping
    public ResponseEntity<UserResponse> createProfile(@RequestBody UserRequest request) throws JsonProcessingException {
        return ResponseEntity.ok(service.createUser(request));
    }

    @GetMapping
    public ResponseEntity<List<ProfileResponse>> getAllProfile() {
        return ResponseEntity.ok(service.getAllUser());
    }
}
