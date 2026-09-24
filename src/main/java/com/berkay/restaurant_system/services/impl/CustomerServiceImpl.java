package com.berkay.restaurant_system.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.berkay.restaurant_system.dtos.CustomerDto;
import com.berkay.restaurant_system.entities.Customer;
import com.berkay.restaurant_system.exceptions.BadRequestException;
import com.berkay.restaurant_system.exceptions.ResourceNotFoundException;
import com.berkay.restaurant_system.mappers.CustomerMapper;
import com.berkay.restaurant_system.repositories.CustomerRepository;
import com.berkay.restaurant_system.repositories.RestaurantTableRepository;
import com.berkay.restaurant_system.services.CustomerService;

@Service 
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final RestaurantTableRepository restaurantTableRepository; // Masayı doğrulamak için eklendi

    public CustomerServiceImpl(CustomerRepository customerRepository, RestaurantTableRepository restaurantTableRepository) {
        this.customerRepository = customerRepository;
        this.restaurantTableRepository = restaurantTableRepository;
    }

    @Override
    public List<CustomerDto> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();
        return customers.stream()
                .map(CustomerMapper::mapToCustomerDto)
                .collect(Collectors.toList());
    }

    @Override
    public CustomerDto getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));
        return CustomerMapper.mapToCustomerDto(customer);
    }

    @Override
    public CustomerDto addCustomer(CustomerDto customerDto) {
        // Eğer bir masa atanmışsa, o masanın gerçekten var olup olmadığını kontrol et
        if (customerDto.getTableId() != null) {
            boolean tableExists = restaurantTableRepository.existsById(customerDto.getTableId());
            if (!tableExists) {
                throw new BadRequestException("Assigned table does not exist with id: " + customerDto.getTableId());
            }
        }

        Customer customer = CustomerMapper.mapToCustomer(customerDto);
        Customer savedCustomer = customerRepository.save(customer);
        return CustomerMapper.mapToCustomerDto(savedCustomer);
    }

    @Override
    public CustomerDto updateCustomer(Long id, CustomerDto customerDto) {
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));
        
        if (customerDto.getTableId() != null) {
            boolean tableExists = restaurantTableRepository.existsById(customerDto.getTableId());
            if (!tableExists) {
                throw new BadRequestException("Assigned table does not exist with id: " + customerDto.getTableId());
            }
        }
        
        Customer mappedCustomer = CustomerMapper.mapToCustomer(customerDto);
        existingCustomer.setFirstName(mappedCustomer.getFirstName());
        existingCustomer.setLastName(mappedCustomer.getLastName());
        existingCustomer.setPhoneNumber(mappedCustomer.getPhoneNumber());
        
        if(mappedCustomer.getRestaurantTable() != null) {
            existingCustomer.setRestaurantTable(mappedCustomer.getRestaurantTable());
        }
        
        Customer updatedCustomer = customerRepository.save(existingCustomer);
        return CustomerMapper.mapToCustomerDto(updatedCustomer);
    }

    @Override
    public void deleteCustomer(Long id) {
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));
                
        customerRepository.delete(existingCustomer);
    }
}