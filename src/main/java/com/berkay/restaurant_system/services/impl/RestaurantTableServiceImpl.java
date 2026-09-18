package com.berkay.restaurant_system.services.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.berkay.restaurant_system.entities.RestaurantTable;
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
    public Optional<RestaurantTable> getTableById(Long id) {
        return restaurantTableRepository.findById(id);
    }

    @Override
    public RestaurantTable addTable(RestaurantTable restaurantTable) {
        return restaurantTableRepository.save(restaurantTable);
    }

    @Override
    public RestaurantTable updateTable(Long id, RestaurantTable restaurantTable) {
        if (restaurantTableRepository.existsById(id)) {
            restaurantTable.setId(id);
            return restaurantTableRepository.save(restaurantTable);
        }
        throw new RuntimeException("Table not found with id: " + id);
    }

    @Override
    public void deleteTable(Long id) {
        restaurantTableRepository.deleteById(id);
    }
}