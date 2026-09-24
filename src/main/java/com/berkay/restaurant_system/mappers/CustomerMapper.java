package com.berkay.restaurant_system.mappers;

import com.berkay.restaurant_system.dtos.CustomerDto;
import com.berkay.restaurant_system.entities.Customer;
import com.berkay.restaurant_system.entities.RestaurantTable;

public class CustomerMapper {

    public static CustomerDto mapToCustomerDto(Customer customer) {
        Long tableId = (customer.getRestaurantTable() != null) ? customer.getRestaurantTable().getId() : null;
        
        return new CustomerDto(
                customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getPhoneNumber(),
                tableId
        );
    }

    public static Customer mapToCustomer(CustomerDto customerDto) {
        Customer customer = new Customer();
        customer.setId(customerDto.getId());
        customer.setFirstName(customerDto.getFirstName());
        customer.setLastName(customerDto.getLastName());
        customer.setPhoneNumber(customerDto.getPhoneNumber());
        
        if (customerDto.getTableId() != null) {
            RestaurantTable table = new RestaurantTable();
            table.setId(customerDto.getTableId());
            customer.setRestaurantTable(table);
        }
        
        return customer;
    }
}