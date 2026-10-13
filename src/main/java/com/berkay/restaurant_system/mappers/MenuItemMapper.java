package com.berkay.restaurant_system.mappers;

import com.berkay.restaurant_system.dtos.MenuItemDto;
import com.berkay.restaurant_system.entities.MenuItem;

public class MenuItemMapper {

    public static MenuItemDto mapToMenuItemDto(MenuItem menuItem) {
        return new MenuItemDto(
                menuItem.getId(),
                menuItem.getName(),
                menuItem.getPrice(),
                menuItem.getCategory().name(),
                menuItem.isAvailable()
        );
    }

    public static MenuItem mapToMenuItem(MenuItemDto menuItemDto) {
        MenuItem menuItem = new MenuItem();
        menuItem.setId(menuItemDto.getId());
        menuItem.setName(menuItemDto.getName());
        menuItem.setPrice(menuItemDto.getPrice());

        // category is parsed and available is defaulted by the service layer,
        // since invalid values need to be reported as a BadRequestException.
        return menuItem;
    }
}
