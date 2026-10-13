package com.berkay.restaurant_system.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.berkay.restaurant_system.dtos.RestaurantTableDto;
import com.berkay.restaurant_system.dtos.TableOccupancyLogDto;

public interface RestaurantTableService {
    Page<RestaurantTableDto> getAllTables(Pageable pageable, String status);
    RestaurantTableDto getTableById(Long id);
    RestaurantTableDto addTable(RestaurantTableDto restaurantTableDto);
    RestaurantTableDto updateTable(Long id, RestaurantTableDto restaurantTableDto);
    RestaurantTableDto updateTableStatus(Long id, String newStatus);
    void deleteTable(Long id);
    Page<TableOccupancyLogDto> getOccupancyHistory(Long tableId, Pageable pageable);

    // Used internally by other services (e.g. seating/removing a customer) to drive
    // table status transitions and occupancy logging from a single source of truth.
    void occupyTable(Long tableId);
    void releaseTable(Long tableId);
}
