package com.berkay.restaurant_system.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.berkay.restaurant_system.entities.Customer;

@Repository 
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    
}
