package com.berkay.restaurant_system.mappers;

import com.berkay.restaurant_system.dtos.TableOccupancyLogDto;
import com.berkay.restaurant_system.entities.TableOccupancyLog;

public class TableOccupancyLogMapper {

    public static TableOccupancyLogDto mapToTableOccupancyLogDto(TableOccupancyLog log) {
        return new TableOccupancyLogDto(
                log.getId(),
                log.getRestaurantTable().getId(),
                log.getOccupiedAt(),
                log.getFreedAt()
        );
    }
}
