package com.berkay.restaurant_system.services.impl;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.berkay.restaurant_system.dtos.RestaurantTableDto;
import com.berkay.restaurant_system.dtos.TableOccupancyLogDto;
import com.berkay.restaurant_system.entities.RestaurantTable;
import com.berkay.restaurant_system.entities.TableOccupancyLog;
import com.berkay.restaurant_system.entities.TableStatus;
import com.berkay.restaurant_system.exceptions.BadRequestException;
import com.berkay.restaurant_system.exceptions.ResourceNotFoundException;
import com.berkay.restaurant_system.mappers.RestaurantTableMapper;
import com.berkay.restaurant_system.mappers.TableOccupancyLogMapper;
import com.berkay.restaurant_system.repositories.RestaurantTableRepository;
import com.berkay.restaurant_system.repositories.TableOccupancyLogRepository;
import com.berkay.restaurant_system.services.RestaurantTableService;

@Service
public class RestaurantTableServiceImpl implements RestaurantTableService {

    private static final Map<TableStatus, EnumSet<TableStatus>> ALLOWED_TRANSITIONS = new EnumMap<>(TableStatus.class);

    static {
        ALLOWED_TRANSITIONS.put(TableStatus.AVAILABLE, EnumSet.of(TableStatus.OCCUPIED, TableStatus.RESERVED, TableStatus.CLEANING));
        ALLOWED_TRANSITIONS.put(TableStatus.OCCUPIED, EnumSet.of(TableStatus.CLEANING));
        ALLOWED_TRANSITIONS.put(TableStatus.RESERVED, EnumSet.of(TableStatus.OCCUPIED, TableStatus.AVAILABLE, TableStatus.CLEANING));
        ALLOWED_TRANSITIONS.put(TableStatus.CLEANING, EnumSet.of(TableStatus.AVAILABLE));
    }

    private final RestaurantTableRepository restaurantTableRepository;
    private final TableOccupancyLogRepository tableOccupancyLogRepository;

    public RestaurantTableServiceImpl(RestaurantTableRepository restaurantTableRepository,
            TableOccupancyLogRepository tableOccupancyLogRepository) {
        this.restaurantTableRepository = restaurantTableRepository;
        this.tableOccupancyLogRepository = tableOccupancyLogRepository;
    }

    @Override
    public Page<RestaurantTableDto> getAllTables(Pageable pageable, String statusValue) {
        if (statusValue == null) {
            return restaurantTableRepository.findAll(pageable)
                    .map(RestaurantTableMapper::mapToRestaurantTableDto);
        }

        TableStatus status = parseStatus(statusValue);
        return restaurantTableRepository.findByStatus(status, pageable)
                .map(RestaurantTableMapper::mapToRestaurantTableDto);
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
        table.setStatus(TableStatus.AVAILABLE);

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
    @Transactional
    public RestaurantTableDto updateTableStatus(Long id, String newStatusValue) {
        RestaurantTable table = restaurantTableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RestaurantTable", "id", id));

        TableStatus newStatus = parseStatus(newStatusValue);
        transition(table, newStatus);

        RestaurantTable updatedTable = restaurantTableRepository.save(table);
        return RestaurantTableMapper.mapToRestaurantTableDto(updatedTable);
    }

    @Override
    @Transactional
    public void occupyTable(Long tableId) {
        RestaurantTable table = restaurantTableRepository.findById(tableId)
                .orElseThrow(() -> new ResourceNotFoundException("RestaurantTable", "id", tableId));

        transition(table, TableStatus.OCCUPIED);
        restaurantTableRepository.save(table);
    }

    @Override
    @Transactional
    public void releaseTable(Long tableId) {
        RestaurantTable table = restaurantTableRepository.findById(tableId)
                .orElseThrow(() -> new ResourceNotFoundException("RestaurantTable", "id", tableId));

        if (table.getStatus() == TableStatus.OCCUPIED) {
            transition(table, TableStatus.CLEANING);
            restaurantTableRepository.save(table);
        }
    }

    @Override
    public void deleteTable(Long id) {
        RestaurantTable existingTable = restaurantTableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RestaurantTable", "id", id));

        restaurantTableRepository.delete(existingTable);
    }

    @Override
    public Page<TableOccupancyLogDto> getOccupancyHistory(Long tableId, Pageable pageable) {
        if (!restaurantTableRepository.existsById(tableId)) {
            throw new ResourceNotFoundException("RestaurantTable", "id", tableId);
        }

        return tableOccupancyLogRepository.findByRestaurantTableIdOrderByOccupiedAtDesc(tableId, pageable)
                .map(TableOccupancyLogMapper::mapToTableOccupancyLogDto);
    }

    private void transition(RestaurantTable table, TableStatus newStatus) {
        TableStatus currentStatus = table.getStatus();
        if (!ALLOWED_TRANSITIONS.get(currentStatus).contains(newStatus)) {
            throw new BadRequestException(
                    "Cannot transition table from " + currentStatus + " to " + newStatus + ".");
        }

        table.setStatus(newStatus);

        if (newStatus == TableStatus.OCCUPIED) {
            TableOccupancyLog log = new TableOccupancyLog();
            log.setRestaurantTable(table);
            log.setOccupiedAt(LocalDateTime.now());
            tableOccupancyLogRepository.save(log);
        } else if (currentStatus == TableStatus.OCCUPIED) {
            tableOccupancyLogRepository.findByRestaurantTableIdAndFreedAtIsNull(table.getId())
                    .ifPresent(log -> {
                        log.setFreedAt(LocalDateTime.now());
                        tableOccupancyLogRepository.save(log);
                    });
        }
    }

    private TableStatus parseStatus(String value) {
        try {
            return TableStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new BadRequestException("Unknown table status: " + value);
        }
    }
}
