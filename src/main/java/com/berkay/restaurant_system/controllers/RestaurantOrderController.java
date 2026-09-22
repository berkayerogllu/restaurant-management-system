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

import com.berkay.restaurant_system.entities.RestaurantOrder;
import com.berkay.restaurant_system.services.RestaurantOrderService;

@RestController
@RequestMapping("/api/orders")
public class RestaurantOrderController {

    private final RestaurantOrderService restaurantOrderService;

    public RestaurantOrderController(RestaurantOrderService restaurantOrderService) {
        this.restaurantOrderService = restaurantOrderService;
    }

    // Retrieve all orders
    @GetMapping
    public ResponseEntity<List<RestaurantOrder>> getAllOrders() {
        return ResponseEntity.ok(restaurantOrderService.getAllOrders());
    }

    // Retrieve a specific order by ID
    @GetMapping("/{id}")
    public ResponseEntity<RestaurantOrder> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(restaurantOrderService.getOrderById(id));
    }

    // Place a new order
    @PostMapping
    public ResponseEntity<RestaurantOrder> addOrder(@RequestBody RestaurantOrder order) {
        return new ResponseEntity<>(restaurantOrderService.addOrder(order), HttpStatus.CREATED);
    }

    // Update an existing order
    @PutMapping("/{id}")
    public ResponseEntity<RestaurantOrder> updateOrder(@PathVariable Long id, @RequestBody RestaurantOrder order) {
        return ResponseEntity.ok(restaurantOrderService.updateOrder(id, order));
    }

    // Cancel or delete an order
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        restaurantOrderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}