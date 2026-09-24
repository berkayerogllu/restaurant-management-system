package com.berkay.restaurant_system.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.berkay.restaurant_system.dtos.MenuItemDto;
import com.berkay.restaurant_system.entities.MenuItem;
import com.berkay.restaurant_system.exceptions.ResourceNotFoundException;
import com.berkay.restaurant_system.mappers.MenuItemMapper;
import com.berkay.restaurant_system.repositories.MenuItemRepository;
import com.berkay.restaurant_system.services.MenuItemService;

@Service
public class MenuItemServiceImpl implements MenuItemService {

    private final MenuItemRepository menuItemRepository;

    public MenuItemServiceImpl(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    @Override
    public List<MenuItemDto> getAllMenuItems() {
        List<MenuItem> menuItems = menuItemRepository.findAll();
        return menuItems.stream()
                .map(MenuItemMapper::mapToMenuItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public MenuItemDto getMenuItemById(Long id) {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MenuItem", "id", id));
        return MenuItemMapper.mapToMenuItemDto(menuItem);
    }

    @Override
    public MenuItemDto addMenuItem(MenuItemDto menuItemDto) {
        MenuItem menuItem = MenuItemMapper.mapToMenuItem(menuItemDto);
        MenuItem savedMenuItem = menuItemRepository.save(menuItem);
        return MenuItemMapper.mapToMenuItemDto(savedMenuItem);
    }

    @Override
    public MenuItemDto updateMenuItem(Long id, MenuItemDto menuItemDto) {
        MenuItem existingItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MenuItem", "id", id));
        
        existingItem.setName(menuItemDto.getName());
        existingItem.setPrice(menuItemDto.getPrice());
        
        MenuItem updatedItem = menuItemRepository.save(existingItem);
        return MenuItemMapper.mapToMenuItemDto(updatedItem);
    }

    @Override
    public void deleteMenuItem(Long id) {
        MenuItem existingItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MenuItem", "id", id));
                
        menuItemRepository.delete(existingItem);
    }
}