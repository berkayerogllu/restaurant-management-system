package com.berkay.restaurant_system.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.berkay.restaurant_system.entities.OrderStatus;
import com.berkay.restaurant_system.entities.RestaurantOrder;

@Repository
public interface RestaurantOrderRepository extends JpaRepository<RestaurantOrder, Long> {

    List<RestaurantOrder> findByStatusInOrderByCreatedAtAsc(List<OrderStatus> statuses);

    Optional<RestaurantOrder> findFirstByRestaurantTableIdAndStatusNotInOrderByCreatedAtDesc(
            Long restaurantTableId, List<OrderStatus> excludedStatuses);

    Page<RestaurantOrder> findByCustomerPhoneOrderByCreatedAtDesc(String customerPhone, Pageable pageable);

    long countByStatusNotIn(List<OrderStatus> statuses);

    long countByStatusIn(List<OrderStatus> statuses);

    long countByStatusAndCreatedAtBetween(OrderStatus status, LocalDateTime from, LocalDateTime to);

    List<RestaurantOrder> findByStatusNotAndCreatedAtBetween(OrderStatus excludedStatus, LocalDateTime from, LocalDateTime to);

    @Query("SELECT COALESCE(SUM(o.totalPrice), 0) FROM RestaurantOrder o "
            + "WHERE o.status = :status AND o.createdAt >= :from AND o.createdAt < :to")
    double sumTotalPriceByStatusAndCreatedAtBetween(
            @Param("status") OrderStatus status, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}
