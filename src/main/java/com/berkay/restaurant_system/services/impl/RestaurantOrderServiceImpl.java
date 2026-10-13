package com.berkay.restaurant_system.services.impl;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.berkay.restaurant_system.dtos.OrderItemDto;
import com.berkay.restaurant_system.dtos.RestaurantOrderDto;
import com.berkay.restaurant_system.entities.MenuItem;
import com.berkay.restaurant_system.entities.OrderItem;
import com.berkay.restaurant_system.entities.OrderStatus;
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

    private static final Map<OrderStatus, EnumSet<OrderStatus>> ALLOWED_TRANSITIONS = new EnumMap<>(OrderStatus.class);
    private static final EnumSet<OrderStatus> ITEM_EDITABLE_STATUSES = EnumSet.of(OrderStatus.RECEIVED, OrderStatus.IN_PREPARATION);

    static {
        ALLOWED_TRANSITIONS.put(OrderStatus.RECEIVED, EnumSet.of(OrderStatus.IN_PREPARATION, OrderStatus.CANCELLED));
        ALLOWED_TRANSITIONS.put(OrderStatus.IN_PREPARATION, EnumSet.of(OrderStatus.READY, OrderStatus.CANCELLED));
        ALLOWED_TRANSITIONS.put(OrderStatus.READY, EnumSet.of(OrderStatus.SERVED, OrderStatus.CANCELLED));
        ALLOWED_TRANSITIONS.put(OrderStatus.SERVED, EnumSet.of(OrderStatus.PAID));
        ALLOWED_TRANSITIONS.put(OrderStatus.PAID, EnumSet.noneOf(OrderStatus.class));
        ALLOWED_TRANSITIONS.put(OrderStatus.CANCELLED, EnumSet.noneOf(OrderStatus.class));
    }

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
    public Page<RestaurantOrderDto> getAllOrders(Pageable pageable) {
        return restaurantOrderRepository.findAll(pageable)
                .map(RestaurantOrderMapper::mapToRestaurantOrderDto);
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

        RestaurantOrder order = RestaurantOrderMapper.mapToRestaurantOrder(orderDto);
        order.setStatus(OrderStatus.RECEIVED);

        List<OrderItem> orderItems = resolveOrderItems(orderDto.getItems(), order, true);
        order.setOrderItems(orderItems);
        order.setTotalPrice(calculateTotalPrice(orderItems));

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
            RestaurantOrder mappedOrder = RestaurantOrderMapper.mapToRestaurantOrder(orderDto);
            existingOrder.setRestaurantTable(mappedOrder.getRestaurantTable());
        }

        existingOrder.setDetails(orderDto.getDetails());

        if (orderDto.getItems() != null) {
            if (!ITEM_EDITABLE_STATUSES.contains(existingOrder.getStatus())) {
                throw new BadRequestException(
                        "Cannot modify items of an order in status " + existingOrder.getStatus() + ".");
            }

            existingOrder.getOrderItems().clear();
            List<OrderItem> newItems = resolveOrderItems(orderDto.getItems(), existingOrder, true);
            existingOrder.getOrderItems().addAll(newItems);
            existingOrder.setTotalPrice(calculateTotalPrice(existingOrder.getOrderItems()));
        }

        RestaurantOrder updatedOrder = restaurantOrderRepository.save(existingOrder);
        return RestaurantOrderMapper.mapToRestaurantOrderDto(updatedOrder);
    }

    @Override
    public RestaurantOrderDto updateOrderStatus(Long id, String newStatusValue) {
        RestaurantOrder existingOrder = restaurantOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RestaurantOrder", "id", id));

        OrderStatus newStatus = parseStatus(newStatusValue);
        OrderStatus currentStatus = existingOrder.getStatus();

        if (!ALLOWED_TRANSITIONS.get(currentStatus).contains(newStatus)) {
            throw new BadRequestException(
                    "Cannot transition order from " + currentStatus + " to " + newStatus + ".");
        }

        existingOrder.setStatus(newStatus);
        RestaurantOrder updatedOrder = restaurantOrderRepository.save(existingOrder);
        return RestaurantOrderMapper.mapToRestaurantOrderDto(updatedOrder);
    }

    @Override
    public void deleteOrder(Long id) {
        RestaurantOrder existingOrder = restaurantOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RestaurantOrder", "id", id));

        restaurantOrderRepository.delete(existingOrder);
    }

    private List<OrderItem> resolveOrderItems(List<OrderItemDto> itemDtos, RestaurantOrder owningOrder, boolean requireNonEmpty) {
        if (requireNonEmpty && (itemDtos == null || itemDtos.isEmpty())) {
            throw new BadRequestException("Order must contain at least one item.");
        }

        List<Long> menuItemIds = itemDtos.stream().map(OrderItemDto::getMenuItemId).collect(Collectors.toList());
        Map<Long, MenuItem> menuItemsById = menuItemRepository.findAllById(menuItemIds).stream()
                .collect(Collectors.toMap(MenuItem::getId, menuItem -> menuItem));

        if (menuItemsById.size() != menuItemIds.stream().distinct().count()) {
            throw new BadRequestException("One or more menu item ids do not exist: " + menuItemIds);
        }

        List<String> unavailableItemNames = menuItemsById.values().stream()
                .filter(menuItem -> !menuItem.isAvailable())
                .map(MenuItem::getName)
                .collect(Collectors.toList());
        if (!unavailableItemNames.isEmpty()) {
            throw new BadRequestException("The following menu items are currently unavailable: " + unavailableItemNames);
        }

        return itemDtos.stream().map(itemDto -> {
            MenuItem menuItem = menuItemsById.get(itemDto.getMenuItemId());
            OrderItem orderItem = new OrderItem();
            orderItem.setMenuItem(menuItem);
            orderItem.setQuantity(itemDto.getQuantity());
            orderItem.setUnitPriceAtOrderTime(menuItem.getPrice());
            orderItem.setNote(itemDto.getNote());
            orderItem.setRestaurantOrder(owningOrder);
            return orderItem;
        }).collect(Collectors.toList());
    }

    private double calculateTotalPrice(List<OrderItem> orderItems) {
        return orderItems.stream()
                .mapToDouble(item -> item.getUnitPriceAtOrderTime() * item.getQuantity())
                .sum();
    }

    private OrderStatus parseStatus(String value) {
        try {
            return OrderStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException exception) {
            throw new BadRequestException("Unknown order status: " + value);
        }
    }
}
