package com.berkay.restaurant_system.services;

import java.util.List;

import com.berkay.restaurant_system.dtos.ReservationDto;

public interface ReservationService {
    List<ReservationDto> getAllReservations();
    ReservationDto getReservationById(Long id);
    ReservationDto addReservation(ReservationDto reservationDto);
    ReservationDto updateReservation(Long id, ReservationDto reservationDto);
    void deleteReservation(Long id);
}
