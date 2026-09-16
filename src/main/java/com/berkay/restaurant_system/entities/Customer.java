package com.berkay.restaurant_system.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@NoArgsConstructor 
@Entity 
public class Customer {

    @Id 
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)  
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column (unique = false, nullable = false)
    private String firstName;

    @Column (unique = false, nullable = false)
    private String lastName;

    @Column (unique = true, nullable = false)
    private String phoneNumber;
    
    @OneToOne 
    @JoinColumn (name = "restaurant_table_id")
    private RestaurantTable restaurantTable;

    
}
