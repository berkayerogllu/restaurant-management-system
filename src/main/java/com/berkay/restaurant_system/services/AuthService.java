package com.berkay.restaurant_system.services;

import com.berkay.restaurant_system.dtos.LoginRequestDto;
import com.berkay.restaurant_system.dtos.LoginResponseDto;
import com.berkay.restaurant_system.dtos.StaffCreateDto;
import com.berkay.restaurant_system.dtos.StaffDto;

public interface AuthService {
    LoginResponseDto login(LoginRequestDto loginRequestDto);
    StaffDto register(StaffCreateDto staffCreateDto);
}
