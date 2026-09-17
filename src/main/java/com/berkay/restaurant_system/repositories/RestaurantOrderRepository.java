package com.berkay.restaurant_system.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.berkay.restaurant_system.entities.RestaurantOrder;

@Repository 
public interface RestaurantOrderRepository extends JpaRepository<RestaurantOrder, Long> {
    
}
