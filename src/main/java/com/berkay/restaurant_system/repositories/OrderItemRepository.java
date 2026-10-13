package com.berkay.restaurant_system.repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.berkay.restaurant_system.entities.OrderItem;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("SELECT oi.menuItem.id, oi.menuItem.name, SUM(oi.quantity), SUM(oi.quantity * oi.unitPriceAtOrderTime) "
            + "FROM OrderItem oi "
            + "WHERE oi.restaurantOrder.status <> com.berkay.restaurant_system.entities.OrderStatus.CANCELLED "
            + "GROUP BY oi.menuItem.id, oi.menuItem.name "
            + "ORDER BY SUM(oi.quantity) DESC")
    List<Object[]> findTopSellingMenuItems(Pageable pageable);
}
