package com.berkay.restaurant_system.mappers;

import java.util.List;
import java.util.stream.Collectors;

import com.berkay.restaurant_system.dtos.RestaurantOrderDto;
import com.berkay.restaurant_system.entities.RestaurantOrder;
import com.berkay.restaurant_system.entities.RestaurantTable;

public class RestaurantOrderMapper {

    public static RestaurantOrderDto mapToRestaurantOrderDto(RestaurantOrder order) {
        Long tableId = (order.getRestaurantTable() != null) ? order.getRestaurantTable().getId() : null;

        List<com.berkay.restaurant_system.dtos.OrderItemDto> items = order.getOrderItems().stream()
                .map(OrderItemMapper::mapToOrderItemDto)
                .collect(Collectors.toList());

        return new RestaurantOrderDto(
                order.getId(),
                order.getDetails(),
                order.getTotalPrice(),
                order.getStatus().name(),
                order.getCancellationReason(),
                order.getCustomerPhone(),
                tableId,
                items
        );
    }

    public static RestaurantOrder mapToRestaurantOrder(RestaurantOrderDto orderDto) {
        RestaurantOrder order = new RestaurantOrder();
        order.setId(orderDto.getId());
        order.setDetails(orderDto.getDetails());

        // Create a reference table object just with the ID to satisfy Hibernate foreign key
        if (orderDto.getTableId() != null) {
            RestaurantTable table = new RestaurantTable();
            table.setId(orderDto.getTableId());
            order.setRestaurantTable(table);
        }

        // status, totalPrice and orderItems are resolved and computed by the service layer,
        // since they depend on fetching MenuItem entities and enforcing lifecycle rules.
        return order;
    }
}
