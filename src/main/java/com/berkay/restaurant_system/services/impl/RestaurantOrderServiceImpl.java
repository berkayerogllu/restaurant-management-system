package com.berkay.restaurant_system.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.berkay.restaurant_system.dtos.RestaurantOrderDto;
import com.berkay.restaurant_system.entities.RestaurantOrder;
import com.berkay.restaurant_system.exceptions.BadRequestException;
import com.berkay.restaurant_system.exceptions.ResourceNotFoundException;
import com.berkay.restaurant_system.mappers.RestaurantOrderMapper;
import com.berkay.restaurant_system.repositories.RestaurantOrderRepository;
import com.berkay.restaurant_system.repositories.RestaurantTableRepository;
import com.berkay.restaurant_system.services.RestaurantOrderService;

@Service
public class RestaurantOrderServiceImpl implements RestaurantOrderService {

    private final RestaurantOrderRepository restaurantOrderRepository;
    private final RestaurantTableRepository restaurantTableRepository;

    public RestaurantOrderServiceImpl(RestaurantOrderRepository restaurantOrderRepository, RestaurantTableRepository restaurantTableRepository) {
        this.restaurantOrderRepository = restaurantOrderRepository;
        this.restaurantTableRepository = restaurantTableRepository;
    }

    @Override
    public List<RestaurantOrderDto> getAllOrders() {
        List<RestaurantOrder> orders = restaurantOrderRepository.findAll();
        return orders.stream()
                .map(RestaurantOrderMapper::mapToRestaurantOrderDto)
                .collect(Collectors.toList());
    }

    @Override
    public RestaurantOrderDto getOrderById(Long id) {
        RestaurantOrder order = restaurantOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RestaurantOrder", "id", id));
        return RestaurantOrderMapper.mapToRestaurantOrderDto(order);
    }

    @Override
    public RestaurantOrderDto addOrder(RestaurantOrderDto orderDto) {
        if (orderDto.getTableId() == null) {
            throw new BadRequestException("Order must be assigned to a valid restaurant table id.");
        }
        
        boolean tableExists = restaurantTableRepository.existsById(orderDto.getTableId());
        if (!tableExists) {
            throw new BadRequestException("Assigned table does not exist with id: " + orderDto.getTableId());
        }
        
        RestaurantOrder order = RestaurantOrderMapper.mapToRestaurantOrder(orderDto);
        RestaurantOrder savedOrder = restaurantOrderRepository.save(order);
        return RestaurantOrderMapper.mapToRestaurantOrderDto(savedOrder);
    }

    @Override
    public RestaurantOrderDto updateOrder(Long id, RestaurantOrderDto orderDto) {
        RestaurantOrder existingOrder = restaurantOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RestaurantOrder", "id", id));
        
        if (orderDto.getTableId() != null) {
            boolean tableExists = restaurantTableRepository.existsById(orderDto.getTableId());
            if (!tableExists) {
                throw new BadRequestException("Assigned table does not exist with id: " + orderDto.getTableId());
            }
        }
        
        RestaurantOrder mappedOrder = RestaurantOrderMapper.mapToRestaurantOrder(orderDto);
        existingOrder.setDetails(mappedOrder.getDetails());
        existingOrder.setTotalPrice(mappedOrder.getTotalPrice());
        
        existingOrder.setPaid(mappedOrder.isPaid()); 
        
        if(mappedOrder.getRestaurantTable() != null) {
             existingOrder.setRestaurantTable(mappedOrder.getRestaurantTable());
        }
        
        RestaurantOrder updatedOrder = restaurantOrderRepository.save(existingOrder);
        return RestaurantOrderMapper.mapToRestaurantOrderDto(updatedOrder);
    }

    @Override
    public void deleteOrder(Long id) {
        RestaurantOrder existingOrder = restaurantOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RestaurantOrder", "id", id));
                
        restaurantOrderRepository.delete(existingOrder);
    }
}