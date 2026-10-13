package com.berkay.restaurant_system.services.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.berkay.restaurant_system.dtos.MenuItemDto;
import com.berkay.restaurant_system.entities.MenuCategory;
import com.berkay.restaurant_system.entities.MenuItem;
import com.berkay.restaurant_system.exceptions.BadRequestException;
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
    public Page<MenuItemDto> getAllMenuItems(Pageable pageable, String categoryValue, Boolean available) {
        MenuCategory category = (categoryValue != null) ? parseCategory(categoryValue) : null;

        Page<MenuItem> page;
        if (category != null && available != null) {
            page = menuItemRepository.findByCategoryAndAvailable(category, available, pageable);
        } else if (category != null) {
            page = menuItemRepository.findByCategory(category, pageable);
        } else if (available != null) {
            page = menuItemRepository.findByAvailable(available, pageable);
        } else {
            page = menuItemRepository.findAll(pageable);
        }

        return page.map(MenuItemMapper::mapToMenuItemDto);
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
        menuItem.setCategory(parseCategory(menuItemDto.getCategory()));
        menuItem.setAvailable(menuItemDto.getAvailable() == null || menuItemDto.getAvailable());

        MenuItem savedMenuItem = menuItemRepository.save(menuItem);
        return MenuItemMapper.mapToMenuItemDto(savedMenuItem);
    }

    @Override
    public MenuItemDto updateMenuItem(Long id, MenuItemDto menuItemDto) {
        MenuItem existingItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MenuItem", "id", id));

        existingItem.setName(menuItemDto.getName());
        existingItem.setPrice(menuItemDto.getPrice());
        existingItem.setCategory(parseCategory(menuItemDto.getCategory()));

        if (menuItemDto.getAvailable() != null) {
            existingItem.setAvailable(menuItemDto.getAvailable());
        }

        MenuItem updatedItem = menuItemRepository.save(existingItem);
        return MenuItemMapper.mapToMenuItemDto(updatedItem);
    }

    @Override
    public void deleteMenuItem(Long id) {
        MenuItem existingItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MenuItem", "id", id));

        menuItemRepository.delete(existingItem);
    }

    private MenuCategory parseCategory(String value) {
        try {
            return MenuCategory.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new BadRequestException("Unknown menu category: " + value);
        }
    }
}
