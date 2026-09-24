package com.berkay.restaurant_system.services;

import java.util.List;

import com.berkay.restaurant_system.dtos.RestaurantOrderDto;

public interface RestaurantOrderService {
    List<RestaurantOrderDto> getAllOrders();
    RestaurantOrderDto getOrderById(Long id);
    RestaurantOrderDto addOrder(RestaurantOrderDto orderDto);
    RestaurantOrderDto updateOrder(Long id, RestaurantOrderDto orderDto);
    void deleteOrder(Long id);
}