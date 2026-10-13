package com.berkay.restaurant_system.services.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.berkay.restaurant_system.dtos.ReservationDto;
import com.berkay.restaurant_system.entities.Reservation;
import com.berkay.restaurant_system.exceptions.BadRequestException;
import com.berkay.restaurant_system.exceptions.ResourceNotFoundException;
import com.berkay.restaurant_system.mappers.ReservationMapper;
import com.berkay.restaurant_system.repositories.ReservationRepository;
import com.berkay.restaurant_system.repositories.RestaurantTableRepository;
import com.berkay.restaurant_system.services.ReservationService;

@Service
public class ReservationServiceImpl implements ReservationService {

    private static final long NO_RESERVATION_TO_EXCLUDE = -1L;

    private final ReservationRepository reservationRepository;
    private final RestaurantTableRepository restaurantTableRepository;

    public ReservationServiceImpl(ReservationRepository reservationRepository,
            RestaurantTableRepository restaurantTableRepository) {
        this.reservationRepository = reservationRepository;
        this.restaurantTableRepository = restaurantTableRepository;
    }

    @Override
    public Page<ReservationDto> getAllReservations(Pageable pageable) {
        return reservationRepository.findAll(pageable)
                .map(ReservationMapper::mapToReservationDto);
    }

    @Override
    public ReservationDto getReservationById(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", "id", id));
        return ReservationMapper.mapToReservationDto(reservation);
    }

    @Override
    public ReservationDto addReservation(ReservationDto reservationDto) {
        validateTimeRange(reservationDto);

        boolean tableExists = restaurantTableRepository.existsById(reservationDto.getTableId());
        if (!tableExists) {
            throw new BadRequestException("Assigned table does not exist with id: " + reservationDto.getTableId());
        }

        ensureNoOverlap(reservationDto, NO_RESERVATION_TO_EXCLUDE);

        Reservation reservation = ReservationMapper.mapToReservation(reservationDto);
        Reservation savedReservation = reservationRepository.save(reservation);
        return ReservationMapper.mapToReservationDto(savedReservation);
    }

    @Override
    public ReservationDto updateReservation(Long id, ReservationDto reservationDto) {
        Reservation existingReservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", "id", id));

        validateTimeRange(reservationDto);

        boolean tableExists = restaurantTableRepository.existsById(reservationDto.getTableId());
        if (!tableExists) {
            throw new BadRequestException("Assigned table does not exist with id: " + reservationDto.getTableId());
        }

        ensureNoOverlap(reservationDto, id);

        Reservation mappedReservation = ReservationMapper.mapToReservation(reservationDto);
        existingReservation.setCustomerName(mappedReservation.getCustomerName());
        existingReservation.setCustomerPhone(mappedReservation.getCustomerPhone());
        existingReservation.setStartTime(mappedReservation.getStartTime());
        existingReservation.setEndTime(mappedReservation.getEndTime());
        existingReservation.setRestaurantTable(mappedReservation.getRestaurantTable());

        Reservation updatedReservation = reservationRepository.save(existingReservation);
        return ReservationMapper.mapToReservationDto(updatedReservation);
    }

    @Override
    public void deleteReservation(Long id) {
        Reservation existingReservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", "id", id));

        reservationRepository.delete(existingReservation);
    }

    private void validateTimeRange(ReservationDto reservationDto) {
        if (!reservationDto.getEndTime().isAfter(reservationDto.getStartTime())) {
            throw new BadRequestException("Reservation end time must be after start time.");
        }
    }

    private void ensureNoOverlap(ReservationDto reservationDto, Long excludedReservationId) {
        boolean hasOverlap = !reservationRepository.findOverlapping(
                reservationDto.getTableId(),
                reservationDto.getStartTime(),
                reservationDto.getEndTime(),
                excludedReservationId).isEmpty();

        if (hasOverlap) {
            throw new BadRequestException("Table with id " + reservationDto.getTableId()
                    + " already has a reservation overlapping this time range.");
        }
    }
}
