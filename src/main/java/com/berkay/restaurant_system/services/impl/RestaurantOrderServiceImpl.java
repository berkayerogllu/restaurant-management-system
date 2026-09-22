package com.berkay.restaurant_system.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.berkay.restaurant_system.entities.RestaurantOrder;
import com.berkay.restaurant_system.exceptions.BadRequestException;
import com.berkay.restaurant_system.exceptions.ResourceNotFoundException;
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
    public RestaurantOrder getOrderById(Long id) {
        return restaurantOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RestaurantOrder", "id", id));
    }

    @Override
    public RestaurantOrder addOrder(RestaurantOrder order) {
        // Check if the assigned table exists in the database
        if (order.getRestaurantTable() == null || order.getRestaurantTable().getId() == null) {
            throw new BadRequestException("Order must be assigned to a valid restaurant table.");
        }
        
        boolean tableExists = restaurantTableRepository.existsById(order.getRestaurantTable().getId());
        if (!tableExists) {
            throw new BadRequestException("Assigned table does not exist with id: " + order.getRestaurantTable().getId());
        }
        
        return restaurantOrderRepository.save(order);
    }

    @Override
    public RestaurantOrder updateOrder(Long id, RestaurantOrder order) {
        RestaurantOrder existingOrder = restaurantOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RestaurantOrder", "id", id));
        
        order.setId(existingOrder.getId());
        return restaurantOrderRepository.save(order);
    }

    @Override
    public void deleteOrder(Long id) {
        RestaurantOrder existingOrder = restaurantOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RestaurantOrder", "id", id));
                
        restaurantOrderRepository.delete(existingOrder);
    }
}