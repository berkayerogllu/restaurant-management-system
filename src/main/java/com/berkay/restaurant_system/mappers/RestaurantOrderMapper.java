package com.berkay.restaurant_system.mappers;

import java.util.List;
import java.util.stream.Collectors;

import com.berkay.restaurant_system.dtos.RestaurantOrderDto;
import com.berkay.restaurant_system.entities.MenuItem;
import com.berkay.restaurant_system.entities.RestaurantOrder;
import com.berkay.restaurant_system.entities.RestaurantTable;

public class RestaurantOrderMapper {

    public static RestaurantOrderDto mapToRestaurantOrderDto(RestaurantOrder order) {
        Long tableId = (order.getRestaurantTable() != null) ? order.getRestaurantTable().getId() : null;

        List<Long> menuItemIds = (order.getMenuItems() != null)
                ? order.getMenuItems().stream().map(MenuItem::getId).collect(Collectors.toList())
                : List.of();

        return new RestaurantOrderDto(
                order.getId(),
                order.getDetails(),
                order.getTotalPrice(),
                order.isPaid(),
                tableId,
                menuItemIds
        );
    }

    public static RestaurantOrder mapToRestaurantOrder(RestaurantOrderDto orderDto) {
        RestaurantOrder order = new RestaurantOrder();
        order.setId(orderDto.getId());
        order.setDetails(orderDto.getDetails());
        order.setPaid(orderDto.getIsPaid() != null && orderDto.getIsPaid());

        // Create a reference table object just with the ID to satisfy Hibernate foreign key
        if (orderDto.getTableId() != null) {
            RestaurantTable table = new RestaurantTable();
            table.setId(orderDto.getTableId());
            order.setRestaurantTable(table);
        }

        // totalPrice and menuItems are resolved and computed by the service layer,
        // since they depend on fetching MenuItem entities from the repository.
        return order;
    }
}
