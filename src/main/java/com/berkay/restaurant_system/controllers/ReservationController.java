package com.berkay.restaurant_system.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.berkay.restaurant_system.dtos.CustomerDto;
import com.berkay.restaurant_system.dtos.ReservationDto;
import com.berkay.restaurant_system.services.ReservationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    public ResponseEntity<Page<ReservationDto>> getAllReservations(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(reservationService.getAllReservations(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationDto> getReservationById(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.getReservationById(id));
    }

    @GetMapping("/today")
    public ResponseEntity<Page<ReservationDto>> getTodaysReservations(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(reservationService.getTodaysReservations(pageable));
    }

    @GetMapping("/no-show")
    public ResponseEntity<Page<ReservationDto>> getNoShowReservations(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(reservationService.getNoShowReservations(pageable));
    }

    @PostMapping
    public ResponseEntity<ReservationDto> addReservation(@Valid @RequestBody ReservationDto reservationDto) {
        return new ResponseEntity<>(reservationService.addReservation(reservationDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservationDto> updateReservation(@PathVariable Long id,
            @Valid @RequestBody ReservationDto reservationDto) {
        return ResponseEntity.ok(reservationService.updateReservation(id, reservationDto));
    }

    @PostMapping("/{id}/check-in")
    public ResponseEntity<CustomerDto> checkIn(@PathVariable Long id) {
        return new ResponseEntity<>(reservationService.checkIn(id), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }
}
