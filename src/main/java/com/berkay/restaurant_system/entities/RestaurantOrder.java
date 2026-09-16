package com.berkay.restaurant_system.entities;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@NoArgsConstructor 
@Entity 
public class RestaurantOrder {

    @Id 
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @Column (name = "id", nullable = false, updatable = false)
    private Long id;

    @Column (nullable = false)
    private String details;

    @Column (nullable = false)
    private double totalPrice;

    @Column (nullable = false)
    private boolean isPaid;

    @ManyToOne
    @JoinColumn (name = "restaurant_table_id")
    private RestaurantTable restaurantTable;
    
    @ManyToMany
    @JoinTable(
        name = "restaurant_order_menu_items",
        joinColumns = @JoinColumn(name = "restaurant_order_id"),
        inverseJoinColumns = @JoinColumn(name = "menu_item_id")
    )
    private List<MenuItem> menuItems;
}
