package com.berkay.restaurant_system.services.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.berkay.restaurant_system.entities.MenuItem;
import com.berkay.restaurant_system.repositories.MenuItemRepository;
import com.berkay.restaurant_system.services.MenuItemService;

@Service
public class MenuItemServiceImpl implements MenuItemService {

    private final MenuItemRepository menuItemRepository;

    public MenuItemServiceImpl(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    @Override
    public List<MenuItem> getAllMenuItems() {
        return menuItemRepository.findAll();
    }

    @Override
    public Optional<MenuItem> getMenuItemById(Long id) {
        return menuItemRepository.findById(id);
    }

    @Override
    public MenuItem addMenuItem(MenuItem menuItem) {
        return menuItemRepository.save(menuItem);
    }

    @Override
    public MenuItem updateMenuItem(Long id, MenuItem menuItem) {
        if (menuItemRepository.existsById(id)) {
            menuItem.setId(id);
            return menuItemRepository.save(menuItem);
        }
        throw new RuntimeException("Menu Item not found with id: " + id);
    }

    @Override
    public void deleteMenuItem(Long id) {
        menuItemRepository.deleteById(id);
    }
}