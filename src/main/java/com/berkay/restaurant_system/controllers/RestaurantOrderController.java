package com.berkay.restaurant_system.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.berkay.restaurant_system.dtos.RestaurantOrderDto;
import com.berkay.restaurant_system.services.RestaurantOrderService;

@RestController
@RequestMapping("/api/orders")
public class RestaurantOrderController {

    private final RestaurantOrderService restaurantOrderService;

    public RestaurantOrderController(RestaurantOrderService restaurantOrderService) {
        this.restaurantOrderService = restaurantOrderService;
    }

    @GetMapping
    public ResponseEntity<Page<RestaurantOrderDto>> getAllOrders(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(restaurantOrderService.getAllOrders(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantOrderDto> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(restaurantOrderService.getOrderById(id));
    }

    @PostMapping
    public ResponseEntity<RestaurantOrderDto> addOrder(@Valid @RequestBody RestaurantOrderDto orderDto) {
        return new ResponseEntity<>(restaurantOrderService.addOrder(orderDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RestaurantOrderDto> updateOrder(@PathVariable Long id, @Valid @RequestBody RestaurantOrderDto orderDto) {
        return ResponseEntity.ok(restaurantOrderService.updateOrder(id, orderDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        restaurantOrderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}