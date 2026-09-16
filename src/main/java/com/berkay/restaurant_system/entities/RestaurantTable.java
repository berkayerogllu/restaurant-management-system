package com.berkay.restaurant_system.entities;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class RestaurantTable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "id", nullable = false, updatable = false)
    private Long id;

    @Column (unique = true, nullable = false)
    private String tableNumber;

    @Column (nullable = false)
    private int capacity;

} 