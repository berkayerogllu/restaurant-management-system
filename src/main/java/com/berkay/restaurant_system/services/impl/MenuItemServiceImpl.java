package com.berkay.restaurant_system.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.berkay.restaurant_system.entities.MenuItem;
import com.berkay.restaurant_system.exceptions.ResourceNotFoundException;
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
    public MenuItem getMenuItemById(Long id) {
        return menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MenuItem", "id", id));
    }

    @Override
    public MenuItem addMenuItem(MenuItem menuItem) {
        return menuItemRepository.save(menuItem);
    }

    @Override
    public MenuItem updateMenuItem(Long id, MenuItem menuItem) {
        MenuItem existingItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MenuItem", "id", id));
        
        menuItem.setId(existingItem.getId());
        return menuItemRepository.save(menuItem);
    }

    @Override
    public void deleteMenuItem(Long id) {
        MenuItem existingItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MenuItem", "id", id));
                
        menuItemRepository.delete(existingItem);
    }
}