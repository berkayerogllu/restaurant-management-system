package com.berkay.restaurant_system.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopMenuItemDto {
    private Long menuItemId;
    private String name;
    private long quantitySold;
    private double revenue;
}
