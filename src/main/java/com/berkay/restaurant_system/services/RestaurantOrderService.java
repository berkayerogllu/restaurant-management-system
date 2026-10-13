package com.berkay.restaurant_system.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.berkay.restaurant_system.dtos.RestaurantOrderDto;

public interface RestaurantOrderService {
    Page<RestaurantOrderDto> getAllOrders(Pageable pageable);
    RestaurantOrderDto getOrderById(Long id);
    RestaurantOrderDto addOrder(RestaurantOrderDto orderDto);
    RestaurantOrderDto updateOrder(Long id, RestaurantOrderDto orderDto);
    RestaurantOrderDto updateOrderStatus(Long id, String newStatus, String reason);
    void deleteOrder(Long id);

    // Kitchen display queue: RECEIVED/IN_PREPARATION orders, oldest first
    List<RestaurantOrderDto> getKitchenQueue();

    // The current non-finished order for a table, if any
    RestaurantOrderDto getActiveOrderForTable(Long tableId);
}
