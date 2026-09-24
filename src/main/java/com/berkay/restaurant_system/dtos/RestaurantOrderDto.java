package com.berkay.restaurant_system.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantOrderDto {
    private Long id;
    private String details;
    private Double totalPrice;
    private Boolean isPaid;
    private Long tableId;
}