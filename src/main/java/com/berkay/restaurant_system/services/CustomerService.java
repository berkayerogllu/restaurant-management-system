package com.berkay.restaurant_system.services;

import java.util.List;
import java.util.Optional;

import com.berkay.restaurant_system.entities.Customer;

public interface CustomerService {
    List<Customer> getAllCustomers();
    Optional<Customer> getCustomerById(Long id);
    Customer addCustomer(Customer customer);
    Customer updateCustomer(Long id, Customer customer);
    void deleteCustomer(Long id);
}