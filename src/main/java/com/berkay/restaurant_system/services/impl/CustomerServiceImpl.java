package com.berkay.restaurant_system.services.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.berkay.restaurant_system.entities.Customer;
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
    public Optional<Customer> getCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    @Override
    public Customer addCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    @Override
    public Customer updateCustomer(Long id, Customer customer) {
        if (customerRepository.existsById(id)) {
            customer.setId(id);
            return customerRepository.save(customer);
        }
        throw new RuntimeException("Customer not found with id: " + id);
    }

    @Override
    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }
    

}
