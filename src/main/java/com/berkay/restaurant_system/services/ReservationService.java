package com.berkay.restaurant_system.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.berkay.restaurant_system.dtos.ReservationDto;

public interface ReservationService {
    Page<ReservationDto> getAllReservations(Pageable pageable);
    ReservationDto getReservationById(Long id);
    ReservationDto addReservation(ReservationDto reservationDto);
    ReservationDto updateReservation(Long id, ReservationDto reservationDto);
    void deleteReservation(Long id);
}
