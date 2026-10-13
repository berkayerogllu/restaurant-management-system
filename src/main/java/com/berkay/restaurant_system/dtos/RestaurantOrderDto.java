package com.berkay.restaurant_system.dtos;

import java.util.List;

import jakarta.validation.Valid;
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

    // Computed by the server from order items, ignored on requests
    private Double totalPrice;

    // Set by the server on creation and only changed through the status transition endpoint
    private String status;

    // Populated on responses only when the order was cancelled, via the status transition endpoint
    private String cancellationReason;

    private Long tableId;

    @Valid
    private List<OrderItemDto> items;
}
