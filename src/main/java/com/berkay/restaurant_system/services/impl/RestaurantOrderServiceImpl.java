package com.berkay.restaurant_system.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.berkay.restaurant_system.dtos.RestaurantOrderDto;
import com.berkay.restaurant_system.entities.MenuItem;
import com.berkay.restaurant_system.entities.RestaurantOrder;
import com.berkay.restaurant_system.exceptions.BadRequestException;
import com.berkay.restaurant_system.exceptions.ResourceNotFoundException;
import com.berkay.restaurant_system.mappers.RestaurantOrderMapper;
import com.berkay.restaurant_system.repositories.MenuItemRepository;
import com.berkay.restaurant_system.repositories.RestaurantOrderRepository;
import com.berkay.restaurant_system.repositories.RestaurantTableRepository;
import com.berkay.restaurant_system.services.RestaurantOrderService;

@Service
public class RestaurantOrderServiceImpl implements RestaurantOrderService {

    private final RestaurantOrderRepository restaurantOrderRepository;
    private final RestaurantTableRepository restaurantTableRepository;
    private final MenuItemRepository menuItemRepository;

    public RestaurantOrderServiceImpl(RestaurantOrderRepository restaurantOrderRepository,
            RestaurantTableRepository restaurantTableRepository,
            MenuItemRepository menuItemRepository) {
        this.restaurantOrderRepository = restaurantOrderRepository;
        this.restaurantTableRepository = restaurantTableRepository;
        this.menuItemRepository = menuItemRepository;
    }

    @Override
    public List<RestaurantOrderDto> getAllOrders() {
        List<RestaurantOrder> orders = restaurantOrderRepository.findAll();
        return orders.stream()
                .map(RestaurantOrderMapper::mapToRestaurantOrderDto)
                .collect(Collectors.toList());
    }

    @Override
    public RestaurantOrderDto getOrderById(Long id) {
        RestaurantOrder order = restaurantOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RestaurantOrder", "id", id));
        return RestaurantOrderMapper.mapToRestaurantOrderDto(order);
    }

    @Override
    public RestaurantOrderDto addOrder(RestaurantOrderDto orderDto) {
        if (orderDto.getTableId() == null) {
            throw new BadRequestException("Order must be assigned to a valid restaurant table id.");
        }

        boolean tableExists = restaurantTableRepository.existsById(orderDto.getTableId());
        if (!tableExists) {
            throw new BadRequestException("Assigned table does not exist with id: " + orderDto.getTableId());
        }

        List<MenuItem> menuItems = resolveMenuItems(orderDto.getMenuItemIds(), true);

        RestaurantOrder order = RestaurantOrderMapper.mapToRestaurantOrder(orderDto);
        order.setMenuItems(menuItems);
        order.setTotalPrice(calculateTotalPrice(menuItems));

        RestaurantOrder savedOrder = restaurantOrderRepository.save(order);
        return RestaurantOrderMapper.mapToRestaurantOrderDto(savedOrder);
    }

    @Override
    public RestaurantOrderDto updateOrder(Long id, RestaurantOrderDto orderDto) {
        RestaurantOrder existingOrder = restaurantOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RestaurantOrder", "id", id));

        if (orderDto.getTableId() != null) {
            boolean tableExists = restaurantTableRepository.existsById(orderDto.getTableId());
            if (!tableExists) {
                throw new BadRequestException("Assigned table does not exist with id: " + orderDto.getTableId());
            }
        }

        RestaurantOrder mappedOrder = RestaurantOrderMapper.mapToRestaurantOrder(orderDto);
        existingOrder.setDetails(mappedOrder.getDetails());
        existingOrder.setPaid(mappedOrder.isPaid());

        if (mappedOrder.getRestaurantTable() != null) {
            existingOrder.setRestaurantTable(mappedOrder.getRestaurantTable());
        }

        if (orderDto.getMenuItemIds() != null) {
            List<MenuItem> menuItems = resolveMenuItems(orderDto.getMenuItemIds(), true);
            existingOrder.setMenuItems(menuItems);
        }

        existingOrder.setTotalPrice(calculateTotalPrice(existingOrder.getMenuItems()));

        RestaurantOrder updatedOrder = restaurantOrderRepository.save(existingOrder);
        return RestaurantOrderMapper.mapToRestaurantOrderDto(updatedOrder);
    }

    @Override
    public void deleteOrder(Long id) {
        RestaurantOrder existingOrder = restaurantOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RestaurantOrder", "id", id));

        restaurantOrderRepository.delete(existingOrder);
    }

    private List<MenuItem> resolveMenuItems(List<Long> menuItemIds, boolean requireNonEmpty) {
        if (requireNonEmpty && (menuItemIds == null || menuItemIds.isEmpty())) {
            throw new BadRequestException("Order must contain at least one menu item.");
        }

        List<MenuItem> menuItems = menuItemRepository.findAllById(menuItemIds);
        if (menuItems.size() != menuItemIds.size()) {
            throw new BadRequestException("One or more menu item ids do not exist: " + menuItemIds);
        }

        return menuItems;
    }

    private double calculateTotalPrice(List<MenuItem> menuItems) {
        return menuItems.stream().mapToDouble(MenuItem::getPrice).sum();
    }
}
