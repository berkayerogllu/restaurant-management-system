package com.berkay.restaurant_system.services;

import java.util.List;

import com.berkay.restaurant_system.entities.RestaurantOrder;

public interface RestaurantOrderService {
    List<RestaurantOrder> getAllOrders();
    RestaurantOrder getOrderById(Long id);
    RestaurantOrder addOrder(RestaurantOrder order);
    RestaurantOrder updateOrder(Long id, RestaurantOrder order);
    void deleteOrder(Long id);
}