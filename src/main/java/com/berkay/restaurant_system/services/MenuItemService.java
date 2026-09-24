package com.berkay.restaurant_system.services;

import java.util.List;

import com.berkay.restaurant_system.dtos.MenuItemDto;

public interface MenuItemService {
    List<MenuItemDto> getAllMenuItems();
    MenuItemDto getMenuItemById(Long id);
    MenuItemDto addMenuItem(MenuItemDto menuItemDto);
    MenuItemDto updateMenuItem(Long id, MenuItemDto menuItemDto);
    void deleteMenuItem(Long id);
}