package com.berkay.restaurant_system.services;

import java.util.List;

import com.berkay.restaurant_system.dtos.RestaurantTableDto;

public interface RestaurantTableService {
    List<RestaurantTableDto> getAllTables();
    RestaurantTableDto getTableById(Long id);
    RestaurantTableDto addTable(RestaurantTableDto restaurantTableDto);
    RestaurantTableDto updateTable(Long id, RestaurantTableDto restaurantTableDto);
    void deleteTable(Long id);
}