package com.berkay.restaurant_system.services.impl;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.berkay.restaurant_system.dtos.DashboardSummaryDto;
import com.berkay.restaurant_system.dtos.HourlyOrderCountDto;
import com.berkay.restaurant_system.dtos.RevenueReportDto;
import com.berkay.restaurant_system.dtos.TopMenuItemDto;
import com.berkay.restaurant_system.entities.OrderStatus;
import com.berkay.restaurant_system.entities.RestaurantOrder;
import com.berkay.restaurant_system.entities.TableOccupancyLog;
import com.berkay.restaurant_system.entities.TableStatus;
import com.berkay.restaurant_system.exceptions.BadRequestException;
import com.berkay.restaurant_system.repositories.OrderItemRepository;
import com.berkay.restaurant_system.repositories.RestaurantOrderRepository;
import com.berkay.restaurant_system.repositories.RestaurantTableRepository;
import com.berkay.restaurant_system.repositories.TableOccupancyLogRepository;
import com.berkay.restaurant_system.services.DashboardService;

@Service
public class DashboardServiceImpl implements DashboardService {

    private static final List<OrderStatus> KITCHEN_STATUSES = List.of(OrderStatus.RECEIVED, OrderStatus.IN_PREPARATION);
    private static final List<OrderStatus> FINISHED_STATUSES = List.of(OrderStatus.PAID, OrderStatus.CANCELLED);

    private final RestaurantOrderRepository restaurantOrderRepository;
    private final OrderItemRepository orderItemRepository;
    private final RestaurantTableRepository restaurantTableRepository;
    private final TableOccupancyLogRepository tableOccupancyLogRepository;

    public DashboardServiceImpl(RestaurantOrderRepository restaurantOrderRepository,
            OrderItemRepository orderItemRepository,
            RestaurantTableRepository restaurantTableRepository,
            TableOccupancyLogRepository tableOccupancyLogRepository) {
        this.restaurantOrderRepository = restaurantOrderRepository;
        this.orderItemRepository = orderItemRepository;
        this.restaurantTableRepository = restaurantTableRepository;
        this.tableOccupancyLogRepository = tableOccupancyLogRepository;
    }

    @Override
    public RevenueReportDto getRevenueReport(String period) {
        LocalDateTime[] range = resolvePeriod(period);
        LocalDateTime from = range[0];
        LocalDateTime to = range[1];

        double totalRevenue = restaurantOrderRepository.sumTotalPriceByStatusAndCreatedAtBetween(
                OrderStatus.PAID, from, to);
        long paidOrderCount = restaurantOrderRepository.countByStatusAndCreatedAtBetween(
                OrderStatus.PAID, from, to);
        double averageOrderValue = (paidOrderCount == 0) ? 0 : totalRevenue / paidOrderCount;

        return new RevenueReportDto(from, to, totalRevenue, paidOrderCount, averageOrderValue);
    }

    @Override
    public List<TopMenuItemDto> getTopSellingMenuItems(int limit) {
        if (limit <= 0) {
            throw new BadRequestException("limit must be greater than zero.");
        }

        return orderItemRepository.findTopSellingMenuItems(PageRequest.of(0, limit)).stream()
                .map(row -> new TopMenuItemDto(
                        (Long) row[0],
                        (String) row[1],
                        (Long) row[2],
                        (Double) row[3]))
                .collect(Collectors.toList());
    }

    @Override
    public List<HourlyOrderCountDto> getHourlyOrderDistribution(String period) {
        LocalDateTime[] range = resolvePeriod(period);

        List<RestaurantOrder> orders = restaurantOrderRepository
                .findByStatusNotAndCreatedAtBetween(OrderStatus.CANCELLED, range[0], range[1]);

        Map<Integer, Long> countsByHour = orders.stream()
                .collect(Collectors.groupingBy(
                        order -> order.getCreatedAt().getHour(),
                        Collectors.counting()));

        return IntStream.range(0, 24)
                .mapToObj(hour -> new HourlyOrderCountDto(hour, countsByHour.getOrDefault(hour, 0L)))
                .collect(Collectors.toList());
    }

    @Override
    public DashboardSummaryDto getSummary() {
        long activeOrders = restaurantOrderRepository.countByStatusNotIn(FINISHED_STATUSES);
        long kitchenQueueSize = restaurantOrderRepository.countByStatusIn(KITCHEN_STATUSES);
        long totalTables = restaurantTableRepository.count();
        long availableTables = restaurantTableRepository.countByStatus(TableStatus.AVAILABLE);
        long occupiedTables = restaurantTableRepository.countByStatus(TableStatus.OCCUPIED);
        Double averageOccupancyMinutes = calculateAverageOccupancyMinutes();

        return new DashboardSummaryDto(activeOrders, kitchenQueueSize, totalTables, availableTables,
                occupiedTables, averageOccupancyMinutes);
    }

    private Double calculateAverageOccupancyMinutes() {
        List<TableOccupancyLog> closedLogs = tableOccupancyLogRepository.findByFreedAtIsNotNull();
        if (closedLogs.isEmpty()) {
            return null;
        }

        return closedLogs.stream()
                .mapToLong(log -> Duration.between(log.getOccupiedAt(), log.getFreedAt()).toMinutes())
                .average()
                .orElse(0);
    }

    private LocalDateTime[] resolvePeriod(String period) {
        if (period == null) {
            throw new BadRequestException("period is required (daily, weekly or monthly).");
        }

        LocalDate today = LocalDate.now();
        LocalDateTime startOfTomorrow = today.plusDays(1).atStartOfDay();

        switch (period.toLowerCase()) {
            case "daily":
                return new LocalDateTime[] { today.atStartOfDay(), startOfTomorrow };
            case "weekly":
                return new LocalDateTime[] { today.minusDays(6).atStartOfDay(), startOfTomorrow };
            case "monthly":
                LocalDate firstOfMonth = today.withDayOfMonth(1);
                return new LocalDateTime[] { firstOfMonth.atStartOfDay(), startOfTomorrow };
            default:
                throw new BadRequestException("Unknown period: " + period + ". Use daily, weekly or monthly.");
        }
    }
}
