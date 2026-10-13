package com.berkay.restaurant_system.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantOrderDto {
    private Long id;

    @NotBlank(message = "Details are required")
    private String details;

    @PositiveOrZero(message = "Total price cannot be negative")
    private Double totalPrice;

    private Boolean isPaid;

    private Long tableId;
}
