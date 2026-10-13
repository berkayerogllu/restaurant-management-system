package com.berkay.restaurant_system.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.berkay.restaurant_system.dtos.RestaurantTableDto;

public interface RestaurantTableService {
    Page<RestaurantTableDto> getAllTables(Pageable pageable);
    RestaurantTableDto getTableById(Long id);
    RestaurantTableDto addTable(RestaurantTableDto restaurantTableDto);
    RestaurantTableDto updateTable(Long id, RestaurantTableDto restaurantTableDto);
    void deleteTable(Long id);
}
