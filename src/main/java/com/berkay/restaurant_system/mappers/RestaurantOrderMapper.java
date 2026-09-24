package com.berkay.restaurant_system.mappers;

import com.berkay.restaurant_system.dtos.RestaurantOrderDto;
import com.berkay.restaurant_system.entities.RestaurantOrder;
import com.berkay.restaurant_system.entities.RestaurantTable;

public class RestaurantOrderMapper {

    public static RestaurantOrderDto mapToRestaurantOrderDto(RestaurantOrder order) {
        Long tableId = (order.getRestaurantTable() != null) ? order.getRestaurantTable().getId() : null;
        
        return new RestaurantOrderDto(
                order.getId(),
                order.getDetails(),
                order.getTotalPrice(),
                order.isPaid(),
                tableId
        );
    }

    public static RestaurantOrder mapToRestaurantOrder(RestaurantOrderDto orderDto) {
        RestaurantOrder order = new RestaurantOrder();
        order.setId(orderDto.getId());
        order.setDetails(orderDto.getDetails());
        order.setTotalPrice(orderDto.getTotalPrice());
        order.setPaid(orderDto.getIsPaid());

        // Create a reference table object just with the ID to satisfy Hibernate foreign key
        if (orderDto.getTableId() != null) {
            RestaurantTable table = new RestaurantTable();
            table.setId(orderDto.getTableId());
            order.setRestaurantTable(table);
        }
        
        return order;
    }
}