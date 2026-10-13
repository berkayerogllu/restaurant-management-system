package com.berkay.restaurant_system.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.berkay.restaurant_system.entities.OrderStatus;
import com.berkay.restaurant_system.entities.RestaurantOrder;

@Repository
public interface RestaurantOrderRepository extends JpaRepository<RestaurantOrder, Long> {

    List<RestaurantOrder> findByStatusInOrderByCreatedAtAsc(List<OrderStatus> statuses);

    Optional<RestaurantOrder> findFirstByRestaurantTableIdAndStatusNotInOrderByCreatedAtDesc(
            Long restaurantTableId, List<OrderStatus> excludedStatuses);

    Page<RestaurantOrder> findByCustomerPhoneOrderByCreatedAtDesc(String customerPhone, Pageable pageable);
}
