package com.berkay.restaurant_system.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.berkay.restaurant_system.dtos.RestaurantOrderDto;

public interface RestaurantOrderService {
    Page<RestaurantOrderDto> getAllOrders(Pageable pageable);
    RestaurantOrderDto getOrderById(Long id);
    RestaurantOrderDto addOrder(RestaurantOrderDto orderDto);
    RestaurantOrderDto updateOrder(Long id, RestaurantOrderDto orderDto);
    void deleteOrder(Long id);
}
