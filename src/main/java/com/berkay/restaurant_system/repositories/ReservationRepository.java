package com.berkay.restaurant_system.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.berkay.restaurant_system.entities.Reservation;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByRestaurantTableId(Long restaurantTableId);

    Page<Reservation> findByStartTimeBetweenOrderByStartTimeAsc(LocalDateTime from, LocalDateTime to, Pageable pageable);

    Page<Reservation> findByCheckedInFalseAndStartTimeBeforeOrderByStartTimeAsc(LocalDateTime now, Pageable pageable);

    @Query("SELECT r FROM Reservation r WHERE r.restaurantTable.id = :tableId "
            + "AND r.id <> :excludedReservationId "
            + "AND r.startTime < :endTime AND r.endTime > :startTime")
    List<Reservation> findOverlapping(
            @Param("tableId") Long tableId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("excludedReservationId") Long excludedReservationId);
}
