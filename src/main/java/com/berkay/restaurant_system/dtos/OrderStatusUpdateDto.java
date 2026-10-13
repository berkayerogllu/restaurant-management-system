package com.berkay.restaurant_system.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusUpdateDto {

    @NotBlank(message = "Status is required")
    private String status;

    // Required when transitioning to CANCELLED, ignored otherwise
    private String reason;
}
