package com.berkay.restaurant_system.services.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.berkay.restaurant_system.entities.RestaurantOrder;
import com.berkay.restaurant_system.repositories.RestaurantOrderRepository;
import com.berkay.restaurant_system.repositories.RestaurantTableRepository;
import com.berkay.restaurant_system.services.RestaurantOrderService;

@Service
public class RestaurantOrderServiceImpl implements RestaurantOrderService {

    private final RestaurantOrderRepository restaurantOrderRepository;
    private final RestaurantTableRepository restaurantTableRepository;

    public RestaurantOrderServiceImpl(RestaurantOrderRepository restaurantOrderRepository, RestaurantTableRepository restaurantTableRepository) {
        this.restaurantOrderRepository = restaurantOrderRepository;
        this.restaurantTableRepository = restaurantTableRepository;
    }

    @Override
    public List<RestaurantOrder> getAllOrders() {
        return restaurantOrderRepository.findAll();
    }

    @Override
    public Optional<RestaurantOrder> getOrderById(Long id) {
        return restaurantOrderRepository.findById(id);
    }

    @Override
    public RestaurantOrder addOrder(RestaurantOrder order) {
        // Sipariş bir masaya aitse, o masanın veritabanında olup olmadığını kontrol et 🛡️
        if (order.getRestaurantTable() != null && order.getRestaurantTable().getId() != null) {
            boolean tableExists = restaurantTableRepository.existsById(order.getRestaurantTable().getId());
            if (!tableExists) {
                throw new RuntimeException("Table not found with id: " + order.getRestaurantTable().getId());
            }
        }
        return restaurantOrderRepository.save(order);
    }

    @Override
    public RestaurantOrder updateOrder(Long id, RestaurantOrder order) {
        if (restaurantOrderRepository.existsById(id)) {
            order.setId(id);
            return restaurantOrderRepository.save(order);
        }
        throw new RuntimeException("Order not found with id: " + id);
    }

    @Override
    public void deleteOrder(Long id) {
        restaurantOrderRepository.deleteById(id);
    }
}