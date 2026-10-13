package com.berkay.restaurant_system.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.berkay.restaurant_system.entities.MenuCategory;
import com.berkay.restaurant_system.entities.MenuItem;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    Page<MenuItem> findByCategory(MenuCategory category, Pageable pageable);

    Page<MenuItem> findByAvailable(boolean available, Pageable pageable);

    Page<MenuItem> findByCategoryAndAvailable(MenuCategory category, boolean available, Pageable pageable);
}
