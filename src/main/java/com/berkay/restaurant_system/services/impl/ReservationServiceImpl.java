package com.berkay.restaurant_system.services.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.berkay.restaurant_system.dtos.CustomerDto;
import com.berkay.restaurant_system.dtos.ReservationDto;
import com.berkay.restaurant_system.entities.Reservation;
import com.berkay.restaurant_system.exceptions.BadRequestException;
import com.berkay.restaurant_system.exceptions.ResourceNotFoundException;
import com.berkay.restaurant_system.mappers.ReservationMapper;
import com.berkay.restaurant_system.repositories.ReservationRepository;
import com.berkay.restaurant_system.repositories.RestaurantTableRepository;
import com.berkay.restaurant_system.services.CustomerService;
import com.berkay.restaurant_system.services.ReservationService;

@Service
public class ReservationServiceImpl implements ReservationService {

    private static final long NO_RESERVATION_TO_EXCLUDE = -1L;

    private final ReservationRepository reservationRepository;
    private final RestaurantTableRepository restaurantTableRepository;
    private final CustomerService customerService;

    public ReservationServiceImpl(ReservationRepository reservationRepository,
            RestaurantTableRepository restaurantTableRepository,
            CustomerService customerService) {
        this.reservationRepository = reservationRepository;
        this.restaurantTableRepository = restaurantTableRepository;
        this.customerService = customerService;
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
        reservation.setCheckedIn(false);
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

    @Override
    public Page<ReservationDto> getTodaysReservations(Pageable pageable) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();

        return reservationRepository.findByStartTimeBetweenOrderByStartTimeAsc(startOfDay, endOfDay, pageable)
                .map(ReservationMapper::mapToReservationDto);
    }

    @Override
    public Page<ReservationDto> getNoShowReservations(Pageable pageable) {
        return reservationRepository.findByCheckedInFalseAndStartTimeBeforeOrderByStartTimeAsc(LocalDateTime.now(), pageable)
                .map(ReservationMapper::mapToReservationDto);
    }

    @Override
    @Transactional
    public CustomerDto checkIn(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", "id", reservationId));

        if (reservation.isCheckedIn()) {
            throw new BadRequestException("Reservation with id " + reservationId + " is already checked in.");
        }

        CustomerDto customerDto = new CustomerDto(
                null,
                firstNameOf(reservation.getCustomerName()),
                lastNameOf(reservation.getCustomerName()),
                reservation.getCustomerPhone(),
                reservation.getRestaurantTable().getId()
        );

        CustomerDto seatedCustomer = customerService.addCustomer(customerDto);

        reservation.setCheckedIn(true);
        reservationRepository.save(reservation);

        return seatedCustomer;
    }

    private String firstNameOf(String fullName) {
        String trimmed = fullName.trim();
        int spaceIndex = trimmed.indexOf(' ');
        return spaceIndex == -1 ? trimmed : trimmed.substring(0, spaceIndex);
    }

    private String lastNameOf(String fullName) {
        String trimmed = fullName.trim();
        int spaceIndex = trimmed.indexOf(' ');
        return spaceIndex == -1 ? trimmed : trimmed.substring(spaceIndex + 1).trim();
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
