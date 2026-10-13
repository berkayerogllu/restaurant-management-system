package com.berkay.restaurant_system.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.berkay.restaurant_system.dtos.MenuItemDto;

public interface MenuItemService {
    Page<MenuItemDto> getAllMenuItems(Pageable pageable);
    MenuItemDto getMenuItemById(Long id);
    MenuItemDto addMenuItem(MenuItemDto menuItemDto);
    MenuItemDto updateMenuItem(Long id, MenuItemDto menuItemDto);
    void deleteMenuItem(Long id);
}
