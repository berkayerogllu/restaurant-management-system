package com.berkay.restaurant_system.mappers;

import com.berkay.restaurant_system.dtos.RestaurantTableDto;
import com.berkay.restaurant_system.entities.RestaurantTable;

public class RestaurantTableMapper {

    public static RestaurantTableDto mapToRestaurantTableDto(RestaurantTable table) {
        return new RestaurantTableDto(
                table.getId(),
                table.getTableNumber(),
                table.getCapacity()
        );
    }

    public static RestaurantTable mapToRestaurantTable(RestaurantTableDto tableDto) {
        RestaurantTable table = new RestaurantTable();
        table.setId(tableDto.getId());
        table.setTableNumber(tableDto.getTableNumber());
        table.setCapacity(tableDto.getCapacity());
        return table;
    }
}