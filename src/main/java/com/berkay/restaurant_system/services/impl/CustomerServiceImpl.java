package com.berkay.restaurant_system.services.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.berkay.restaurant_system.dtos.CustomerDto;
import com.berkay.restaurant_system.entities.Customer;
import com.berkay.restaurant_system.exceptions.BadRequestException;
import com.berkay.restaurant_system.exceptions.ResourceNotFoundException;
import com.berkay.restaurant_system.mappers.CustomerMapper;
import com.berkay.restaurant_system.repositories.CustomerRepository;
import com.berkay.restaurant_system.repositories.RestaurantTableRepository;
import com.berkay.restaurant_system.services.CustomerService;
import com.berkay.restaurant_system.services.RestaurantTableService;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final RestaurantTableRepository restaurantTableRepository; // Masayı doğrulamak için eklendi
    private final RestaurantTableService restaurantTableService;

    public CustomerServiceImpl(CustomerRepository customerRepository, RestaurantTableRepository restaurantTableRepository,
            RestaurantTableService restaurantTableService) {
        this.customerRepository = customerRepository;
        this.restaurantTableRepository = restaurantTableRepository;
        this.restaurantTableService = restaurantTableService;
    }

    @Override
    public Page<CustomerDto> getAllCustomers(Pageable pageable) {
        return customerRepository.findAll(pageable)
                .map(CustomerMapper::mapToCustomerDto);
    }

    @Override
    public CustomerDto getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));
        return CustomerMapper.mapToCustomerDto(customer);
    }

    @Override
    @Transactional
    public CustomerDto addCustomer(CustomerDto customerDto) {
        // Eğer bir masa atanmışsa, o masanın gerçekten var olup olmadığını kontrol et
        if (customerDto.getTableId() != null) {
            boolean tableExists = restaurantTableRepository.existsById(customerDto.getTableId());
            if (!tableExists) {
                throw new BadRequestException("Assigned table does not exist with id: " + customerDto.getTableId());
            }

            ensureTableIsNotTakenByAnotherCustomer(customerDto.getTableId(), null);
        }

        Customer customer = CustomerMapper.mapToCustomer(customerDto);
        Customer savedCustomer = customerRepository.save(customer);

        if (customerDto.getTableId() != null) {
            restaurantTableService.occupyTable(customerDto.getTableId());
        }

        return CustomerMapper.mapToCustomerDto(savedCustomer);
    }

    @Override
    @Transactional
    public CustomerDto updateCustomer(Long id, CustomerDto customerDto) {
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));

        Long previousTableId = (existingCustomer.getRestaurantTable() != null)
                ? existingCustomer.getRestaurantTable().getId() : null;

        if (customerDto.getTableId() != null) {
            boolean tableExists = restaurantTableRepository.existsById(customerDto.getTableId());
            if (!tableExists) {
                throw new BadRequestException("Assigned table does not exist with id: " + customerDto.getTableId());
            }

            ensureTableIsNotTakenByAnotherCustomer(customerDto.getTableId(), id);
        }

        Customer mappedCustomer = CustomerMapper.mapToCustomer(customerDto);
        existingCustomer.setFirstName(mappedCustomer.getFirstName());
        existingCustomer.setLastName(mappedCustomer.getLastName());
        existingCustomer.setPhoneNumber(mappedCustomer.getPhoneNumber());

        if(mappedCustomer.getRestaurantTable() != null) {
            existingCustomer.setRestaurantTable(mappedCustomer.getRestaurantTable());
        }

        Customer updatedCustomer = customerRepository.save(existingCustomer);

        boolean tableChanged = customerDto.getTableId() != null && !customerDto.getTableId().equals(previousTableId);
        if (tableChanged) {
            if (previousTableId != null) {
                restaurantTableService.releaseTable(previousTableId);
            }
            restaurantTableService.occupyTable(customerDto.getTableId());
        }

        return CustomerMapper.mapToCustomerDto(updatedCustomer);
    }

    @Override
    @Transactional
    public void deleteCustomer(Long id) {
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));

        Long tableId = (existingCustomer.getRestaurantTable() != null)
                ? existingCustomer.getRestaurantTable().getId() : null;

        customerRepository.delete(existingCustomer);

        if (tableId != null) {
            restaurantTableService.releaseTable(tableId);
        }
    }

    private void ensureTableIsNotTakenByAnotherCustomer(Long tableId, Long currentCustomerId) {
        customerRepository.findByRestaurantTableId(tableId).ifPresent(occupant -> {
            if (!occupant.getId().equals(currentCustomerId)) {
                throw new BadRequestException("Table with id " + tableId + " is already assigned to another customer.");
            }
        });
    }
}
