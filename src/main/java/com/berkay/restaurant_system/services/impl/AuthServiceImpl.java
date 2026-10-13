package com.berkay.restaurant_system.services.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.berkay.restaurant_system.dtos.LoginRequestDto;
import com.berkay.restaurant_system.dtos.LoginResponseDto;
import com.berkay.restaurant_system.dtos.StaffCreateDto;
import com.berkay.restaurant_system.dtos.StaffDto;
import com.berkay.restaurant_system.entities.Staff;
import com.berkay.restaurant_system.exceptions.BadRequestException;
import com.berkay.restaurant_system.mappers.StaffMapper;
import com.berkay.restaurant_system.repositories.StaffRepository;
import com.berkay.restaurant_system.security.JwtService;
import com.berkay.restaurant_system.services.AuthService;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final StaffRepository staffRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(AuthenticationManager authenticationManager, JwtService jwtService,
            StaffRepository staffRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.staffRepository = staffRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword()));

        String token = jwtService.generateToken(loginRequestDto.getUsername());
        return new LoginResponseDto(token, loginRequestDto.getUsername());
    }

    @Override
    public StaffDto register(StaffCreateDto staffCreateDto) {
        if (staffRepository.existsByUsername(staffCreateDto.getUsername())) {
            throw new BadRequestException("Username is already taken: " + staffCreateDto.getUsername());
        }

        Staff staff = new Staff();
        staff.setUsername(staffCreateDto.getUsername());
        staff.setPassword(passwordEncoder.encode(staffCreateDto.getPassword()));

        Staff savedStaff = staffRepository.save(staff);
        return StaffMapper.mapToStaffDto(savedStaff);
    }
}
