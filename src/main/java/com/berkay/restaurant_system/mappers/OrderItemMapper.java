package com.berkay.restaurant_system.mappers;

import com.berkay.restaurant_system.dtos.OrderItemDto;
import com.berkay.restaurant_system.entities.OrderItem;

public class OrderItemMapper {

    public static OrderItemDto mapToOrderItemDto(OrderItem orderItem) {
        return new OrderItemDto(
                orderItem.getId(),
                orderItem.getMenuItem().getId(),
                orderItem.getMenuItem().getName(),
                orderItem.getQuantity(),
                orderItem.getUnitPriceAtOrderTime(),
                orderItem.getNote()
        );
    }
}
