package com.berkay.restaurant_system.dtos;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableOccupancyLogDto {
    private Long id;
    private Long tableId;
    private LocalDateTime occupiedAt;
    private LocalDateTime freedAt;
}
