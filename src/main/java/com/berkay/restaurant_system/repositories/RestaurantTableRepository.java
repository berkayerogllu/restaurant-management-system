package com.berkay.restaurant_system.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.berkay.restaurant_system.entities.RestaurantTable;
import com.berkay.restaurant_system.entities.TableStatus;

@Repository
public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Long> {

    Page<RestaurantTable> findByStatus(TableStatus status, Pageable pageable);
}
