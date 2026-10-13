package com.berkay.restaurant_system.services;

import java.util.List;

import com.berkay.restaurant_system.dtos.DashboardSummaryDto;
import com.berkay.restaurant_system.dtos.HourlyOrderCountDto;
import com.berkay.restaurant_system.dtos.RevenueReportDto;
import com.berkay.restaurant_system.dtos.TopMenuItemDto;

public interface DashboardService {

    // period: "daily", "weekly" or "monthly"
    RevenueReportDto getRevenueReport(String period);

    List<TopMenuItemDto> getTopSellingMenuItems(int limit);

    // Hourly order counts (0-23) over the given period, for orders not CANCELLED
    List<HourlyOrderCountDto> getHourlyOrderDistribution(String period);

    DashboardSummaryDto getSummary();
}
