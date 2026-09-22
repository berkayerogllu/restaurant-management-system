package com.berkay.restaurant_system.services;

import java.util.List;

import com.berkay.restaurant_system.entities.Customer;

public interface CustomerService {
    List<Customer> getAllCustomers();
    Customer getCustomerById(Long id);
    Customer addCustomer(Customer customer);
    Customer updateCustomer(Long id, Customer customer);
    void deleteCustomer(Long id);
}