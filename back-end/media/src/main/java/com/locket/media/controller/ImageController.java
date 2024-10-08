package com.locket.media.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/image")
public class ImageController {
    @GetMapping
    public ResponseEntity<?> helloWorld() {
        return ResponseEntity.ok("Hello World");
    }
}
