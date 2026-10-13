package com.berkay.restaurant_system.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.berkay.restaurant_system.dtos.LoginRequestDto;
import com.berkay.restaurant_system.dtos.LoginResponseDto;
import com.berkay.restaurant_system.dtos.StaffCreateDto;
import com.berkay.restaurant_system.dtos.StaffDto;
import com.berkay.restaurant_system.services.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        return ResponseEntity.ok(authService.login(loginRequestDto));
    }

    // Requires an existing valid token: an already logged-in staff member creates another account.
    // The very first account is created by the startup bootstrap (see StaffBootstrap).
    @PostMapping("/register")
    public ResponseEntity<StaffDto> register(@Valid @RequestBody StaffCreateDto staffCreateDto) {
        return new ResponseEntity<>(authService.register(staffCreateDto), HttpStatus.CREATED);
    }
}
