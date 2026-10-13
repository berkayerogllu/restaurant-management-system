package com.berkay.restaurant_system.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryDto {
    private long activeOrders;
    private long kitchenQueueSize;
    private long totalTables;
    private long availableTables;
    private long occupiedTables;
    // Null when there is no closed occupancy log yet to average over
    private Double averageOccupancyMinutes;
}
