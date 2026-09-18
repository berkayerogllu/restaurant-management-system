package com.berkay.restaurant_system.services;

import java.util.List;
import java.util.Optional;

import com.berkay.restaurant_system.entities.RestaurantOrder;

public interface RestaurantOrderService {
    List<RestaurantOrder> getAllOrders();
    Optional<RestaurantOrder> getOrderById(Long id);
    RestaurantOrder addOrder(RestaurantOrder order);
    RestaurantOrder updateOrder(Long id, RestaurantOrder order);
    void deleteOrder(Long id);
}