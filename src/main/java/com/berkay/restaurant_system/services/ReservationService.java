package com.berkay.restaurant_system.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.berkay.restaurant_system.dtos.CustomerDto;
import com.berkay.restaurant_system.dtos.ReservationDto;

public interface ReservationService {
    Page<ReservationDto> getAllReservations(Pageable pageable);
    ReservationDto getReservationById(Long id);
    ReservationDto addReservation(ReservationDto reservationDto);
    ReservationDto updateReservation(Long id, ReservationDto reservationDto);
    void deleteReservation(Long id);

    // Reservations whose startTime falls within today
    Page<ReservationDto> getTodaysReservations(Pageable pageable);

    // Reservations whose startTime has passed but were never checked in
    Page<ReservationDto> getNoShowReservations(Pageable pageable);

    // Seats the reservation's customer at the reserved table, creating a Customer record
    CustomerDto checkIn(Long reservationId);
}
