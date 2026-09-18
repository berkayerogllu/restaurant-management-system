package com.berkay.restaurant_system.services;

import java.util.List;
import java.util.Optional;

import com.berkay.restaurant_system.entities.RestaurantTable;

public interface RestaurantTableService {
    List<RestaurantTable> getAllTables();
    Optional<RestaurantTable> getTableById(Long id);
    RestaurantTable addTable(RestaurantTable restaurantTable);
    RestaurantTable updateTable(Long id, RestaurantTable restaurantTable);
    void deleteTable(Long id);
}