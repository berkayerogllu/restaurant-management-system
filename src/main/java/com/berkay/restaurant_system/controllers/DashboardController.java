package com.berkay.restaurant_system.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.berkay.restaurant_system.dtos.DashboardSummaryDto;
import com.berkay.restaurant_system.dtos.HourlyOrderCountDto;
import com.berkay.restaurant_system.dtos.RevenueReportDto;
import com.berkay.restaurant_system.dtos.TopMenuItemDto;
import com.berkay.restaurant_system.services.DashboardService;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/revenue")
    public ResponseEntity<RevenueReportDto> getRevenue(@RequestParam String period) {
        return ResponseEntity.ok(dashboardService.getRevenueReport(period));
    }

    @GetMapping("/top-menu-items")
    public ResponseEntity<List<TopMenuItemDto>> getTopMenuItems(
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(dashboardService.getTopSellingMenuItems(limit));
    }

    @GetMapping("/hourly-distribution")
    public ResponseEntity<List<HourlyOrderCountDto>> getHourlyDistribution(@RequestParam String period) {
        return ResponseEntity.ok(dashboardService.getHourlyOrderDistribution(period));
    }

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryDto> getSummary() {
        return ResponseEntity.ok(dashboardService.getSummary());
    }
}
