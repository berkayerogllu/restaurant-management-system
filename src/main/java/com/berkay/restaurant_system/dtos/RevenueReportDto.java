package com.berkay.restaurant_system.dtos;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RevenueReportDto {
    private LocalDateTime from;
    private LocalDateTime to;
    private double totalRevenue;
    private long paidOrderCount;
    private double averageOrderValue;
}
