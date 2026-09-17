package com.berkay.restaurant_system.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.berkay.restaurant_system.entities.MenuItem;

@Repository 
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    
}
