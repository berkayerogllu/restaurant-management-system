package com.berkay.restaurant_system.dtos;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
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

    private Double totalPrice;

    private Boolean isPaid;

    private Long tableId;

    private List<Long> menuItemIds;
}
