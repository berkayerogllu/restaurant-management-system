package com.berkay.restaurant_system.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.berkay.restaurant_system.dtos.RestaurantTableDto;
import com.berkay.restaurant_system.entities.RestaurantTable;
import com.berkay.restaurant_system.exceptions.ResourceNotFoundException;
import com.berkay.restaurant_system.mappers.RestaurantTableMapper;
import com.berkay.restaurant_system.repositories.RestaurantTableRepository;
import com.berkay.restaurant_system.services.RestaurantTableService;

@Service
public class RestaurantTableServiceImpl implements RestaurantTableService {

    private final RestaurantTableRepository restaurantTableRepository;

    public RestaurantTableServiceImpl(RestaurantTableRepository restaurantTableRepository) {
        this.restaurantTableRepository = restaurantTableRepository;
    }

    @Override
    public List<RestaurantTableDto> getAllTables() {
        List<RestaurantTable> tables = restaurantTableRepository.findAll();
        return tables.stream()
                .map(RestaurantTableMapper::mapToRestaurantTableDto)
                .collect(Collectors.toList());
    }

    @Override
    public RestaurantTableDto getTableById(Long id) {
        RestaurantTable table = restaurantTableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RestaurantTable", "id", id));
        return RestaurantTableMapper.mapToRestaurantTableDto(table);
    }

    @Override
    public RestaurantTableDto addTable(RestaurantTableDto restaurantTableDto) {
        RestaurantTable table = RestaurantTableMapper.mapToRestaurantTable(restaurantTableDto);
        RestaurantTable savedTable = restaurantTableRepository.save(table);
        return RestaurantTableMapper.mapToRestaurantTableDto(savedTable);
    }

    @Override
    public RestaurantTableDto updateTable(Long id, RestaurantTableDto restaurantTableDto) {
        RestaurantTable existingTable = restaurantTableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RestaurantTable", "id", id));
        
        existingTable.setTableNumber(restaurantTableDto.getTableNumber());
        existingTable.setCapacity(restaurantTableDto.getCapacity());
        
        RestaurantTable updatedTable = restaurantTableRepository.save(existingTable);
        return RestaurantTableMapper.mapToRestaurantTableDto(updatedTable);
    }

    @Override
    public void deleteTable(Long id) {
        RestaurantTable existingTable = restaurantTableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RestaurantTable", "id", id));
                
        restaurantTableRepository.delete(existingTable);
    }
}