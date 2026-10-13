package com.berkay.restaurant_system.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {
    private Long id;

    @NotNull(message = "Menu item id is required")
    private Long menuItemId;

    // Populated on responses only, ignored on requests
    private String menuItemName;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be greater than zero")
    private Integer quantity;

    // Populated on responses only, ignored on requests
    private Double unitPrice;

    private String note;
}
