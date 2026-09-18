package com.berkay.restaurant_system.services;

import java.util.List;
import java.util.Optional;

import com.berkay.restaurant_system.entities.MenuItem;

public interface MenuItemService {

    List<MenuItem> getAllMenuItems();
    Optional<MenuItem> getMenuItemById(Long id);
    MenuItem addMenuItem(MenuItem menuItem);
    MenuItem updateMenuItem(Long id, MenuItem menuItem);
    void deleteMenuItem(Long id);
}
