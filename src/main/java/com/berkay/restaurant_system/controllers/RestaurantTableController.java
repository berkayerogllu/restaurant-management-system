package com.berkay.restaurant_system.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.berkay.restaurant_system.dtos.RestaurantTableDto;
import com.berkay.restaurant_system.dtos.TableOccupancyLogDto;
import com.berkay.restaurant_system.dtos.TableStatusUpdateDto;
import com.berkay.restaurant_system.services.RestaurantTableService;

@RestController
@RequestMapping("/api/tables")
public class RestaurantTableController {

    private final RestaurantTableService restaurantTableService;

    public RestaurantTableController(RestaurantTableService restaurantTableService) {
        this.restaurantTableService = restaurantTableService;
    }

    @GetMapping
    public ResponseEntity<Page<RestaurantTableDto>> getAllTables(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(restaurantTableService.getAllTables(pageable, status));
    }

    @GetMapping("/available")
    public ResponseEntity<Page<RestaurantTableDto>> getAvailableTables(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(restaurantTableService.getAllTables(pageable, "AVAILABLE"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantTableDto> getTableById(@PathVariable Long id) {
        return ResponseEntity.ok(restaurantTableService.getTableById(id));
    }

    @GetMapping("/{id}/occupancy-history")
    public ResponseEntity<Page<TableOccupancyLogDto>> getOccupancyHistory(@PathVariable Long id,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(restaurantTableService.getOccupancyHistory(id, pageable));
    }

    @PostMapping
    public ResponseEntity<RestaurantTableDto> addTable(@Valid @RequestBody RestaurantTableDto restaurantTableDto) {
        return new ResponseEntity<>(restaurantTableService.addTable(restaurantTableDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RestaurantTableDto> updateTable(@PathVariable Long id, @Valid @RequestBody RestaurantTableDto restaurantTableDto) {
        return ResponseEntity.ok(restaurantTableService.updateTable(id, restaurantTableDto));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<RestaurantTableDto> updateTableStatus(@PathVariable Long id,
            @Valid @RequestBody TableStatusUpdateDto statusUpdateDto) {
        return ResponseEntity.ok(restaurantTableService.updateTableStatus(id, statusUpdateDto.getStatus()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTable(@PathVariable Long id) {
        restaurantTableService.deleteTable(id);
        return ResponseEntity.noContent().build();
    }
}
