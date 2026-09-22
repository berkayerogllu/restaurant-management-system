package com.berkay.restaurant_system.services;

import java.util.List;

import com.berkay.restaurant_system.entities.RestaurantTable;

public interface RestaurantTableService {
    List<RestaurantTable> getAllTables();
    RestaurantTable getTableById(Long id);
    RestaurantTable addTable(RestaurantTable restaurantTable);
    RestaurantTable updateTable(Long id, RestaurantTable restaurantTable);
    void deleteTable(Long id);
}