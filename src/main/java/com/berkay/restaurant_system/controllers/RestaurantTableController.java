package com.berkay.restaurant_system.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.berkay.restaurant_system.entities.RestaurantTable;
import com.berkay.restaurant_system.services.RestaurantTableService;

@RestController
@RequestMapping("/api/tables")
public class RestaurantTableController {

    private final RestaurantTableService restaurantTableService;

    public RestaurantTableController(RestaurantTableService restaurantTableService) {
        this.restaurantTableService = restaurantTableService;
    }

    // Retrieve all restaurant tables
    @GetMapping
    public ResponseEntity<List<RestaurantTable>> getAllTables() {
        return ResponseEntity.ok(restaurantTableService.getAllTables());
    }

    // Retrieve a single table by its ID
    @GetMapping("/{id}")
    public ResponseEntity<RestaurantTable> getTableById(@PathVariable Long id) {
        return ResponseEntity.ok(restaurantTableService.getTableById(id));
    }

    // Add a new table to the restaurant
    @PostMapping
    public ResponseEntity<RestaurantTable> addTable(@RequestBody RestaurantTable restaurantTable) {
        return new ResponseEntity<>(restaurantTableService.addTable(restaurantTable), HttpStatus.CREATED);
    }

    // Update an existing table's details
    @PutMapping("/{id}")
    public ResponseEntity<RestaurantTable> updateTable(@PathVariable Long id, @RequestBody RestaurantTable restaurantTable) {
        return ResponseEntity.ok(restaurantTableService.updateTable(id, restaurantTable));
    }

    // Delete a table
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTable(@PathVariable Long id) {
        restaurantTableService.deleteTable(id);
        return ResponseEntity.noContent().build();
    }
}