package com.berkay.restaurant_system.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.berkay.restaurant_system.entities.Customer;
import com.berkay.restaurant_system.exceptions.ResourceNotFoundException;
import com.berkay.restaurant_system.repositories.CustomerRepository;
import com.berkay.restaurant_system.services.CustomerService;

@Service 
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id).
        orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));
    }

    @Override
    public Customer addCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    @Override
    public Customer updateCustomer(Long id, Customer customer) {
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));
        
        customer.setId(existingCustomer.getId());
        return customerRepository.save(customer);
    }

    @Override
    public void deleteCustomer(Long id) {
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));
                
        customerRepository.delete(existingCustomer);
    }
}
