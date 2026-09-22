package com.berkay.restaurant_system.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.berkay.restaurant_system.entities.RestaurantTable;
import com.berkay.restaurant_system.exceptions.ResourceNotFoundException;
import com.berkay.restaurant_system.repositories.RestaurantTableRepository;
import com.berkay.restaurant_system.services.RestaurantTableService;

@Service
public class RestaurantTableServiceImpl implements RestaurantTableService {

    private final RestaurantTableRepository restaurantTableRepository;

    public RestaurantTableServiceImpl(RestaurantTableRepository restaurantTableRepository) {
        this.restaurantTableRepository = restaurantTableRepository;
    }

    @Override
    public List<RestaurantTable> getAllTables() {
        return restaurantTableRepository.findAll();
    }

    @Override
    public RestaurantTable getTableById(Long id) {
        return restaurantTableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RestaurantTable", "id", id));
    }

    @Override
    public RestaurantTable addTable(RestaurantTable restaurantTable) {
        return restaurantTableRepository.save(restaurantTable);
    }

    @Override
    public RestaurantTable updateTable(Long id, RestaurantTable restaurantTable) {
        RestaurantTable existingTable = restaurantTableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RestaurantTable", "id", id));
        
        restaurantTable.setId(existingTable.getId());
        return restaurantTableRepository.save(restaurantTable);
    }

    @Override
    public void deleteTable(Long id) {
        RestaurantTable existingTable = restaurantTableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RestaurantTable", "id", id));
                
        restaurantTableRepository.delete(existingTable);
    }
}