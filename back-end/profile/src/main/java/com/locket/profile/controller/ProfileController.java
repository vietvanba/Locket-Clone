package com.locket.profile.controller;

import com.locket.profile.payload.ProfileResponse;
import com.locket.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
