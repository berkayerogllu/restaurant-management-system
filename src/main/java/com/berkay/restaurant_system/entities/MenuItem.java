package com.berkay.restaurant_system.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity 
@NoArgsConstructor 
@Data 
public class MenuItem {
    
    @Id
    @GeneratedValue (strategy = jakarta.persistence.GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column (unique = true, nullable = false)
    private String name;

    @Column (nullable = false)
    private double price;

}
