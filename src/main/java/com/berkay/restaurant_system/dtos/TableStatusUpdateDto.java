package com.berkay.restaurant_system.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableStatusUpdateDto {

    @NotBlank(message = "Status is required")
    private String status;
}
