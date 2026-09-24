package com.berkay.restaurant_system.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantTableDto {
    private Long id;
    private String tableNumber;
    private Integer capacity;
}