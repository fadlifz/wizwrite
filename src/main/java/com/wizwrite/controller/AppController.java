package com.wizwrite.controller;

import com.wizwrite.dto.AuthResponse;
import com.wizwrite.dto.ContentRequest;
import com.wizwrite.dto.ContentResponse;
import com.wizwrite.dto.LoginDto;
import com.wizwrite.dto.RegisterDto;
import com.wizwrite.entity.ContentHistory;
import com.wizwrite.entity.UserCredit;
import com.wizwrite.entity.User;
import com.wizwrite.service.AuthService;
import com.wizwrite.service.ContentService;
import com.wizwrite.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class AppController {

    private final AuthService authService;
    private final ContentService contentService;
    private final UserService userService;

    public AppController(AuthService authService, ContentService contentService, UserService userService) {
        this.authService = authService;
        this.contentService = contentService;
        this.userService = userService;
    }

    @PostMapping("/auth/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterDto registerDto) {
        AuthResponse response = authService.register(registerDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginDto loginDto) {
        AuthResponse response = authService.login(loginDto);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/generate")
    public ResponseEntity<ContentResponse> generateContent(@RequestBody ContentRequest contentRequest) {
        ContentResponse response = contentService.generateContent(contentRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<List<ContentHistory>> getContentHistory() {
        List<ContentHistory> history = contentService.getContentHistory();
        return ResponseEntity.ok(history);
    }

    @GetMapping("/credits")
    public ResponseEntity<UserCredit> getUserCredits() {
        UserCredit userCredit = userService.getUserCredits();
        return ResponseEntity.ok(userCredit);
    }
}