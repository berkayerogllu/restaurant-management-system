package com.berkay.restaurant_system.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.berkay.restaurant_system.entities.TableOccupancyLog;

@Repository
public interface TableOccupancyLogRepository extends JpaRepository<TableOccupancyLog, Long> {

    Page<TableOccupancyLog> findByRestaurantTableIdOrderByOccupiedAtDesc(Long restaurantTableId, Pageable pageable);

    Optional<TableOccupancyLog> findByRestaurantTableIdAndFreedAtIsNull(Long restaurantTableId);
}
