package com.berkay.restaurant_system.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@Entity
public class OrderItem {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "menu_item_id", nullable = false)
    private MenuItem menuItem;

    @Column(nullable = false)
    private int quantity;

    // Price of the menu item at the moment this item was ordered, so later
    // menu price changes never affect the total of an already placed order.
    @Column(nullable = false)
    private double unitPriceAtOrderTime;

    @Column
    private String note;

    @ManyToOne
    @JoinColumn(name = "restaurant_order_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private RestaurantOrder restaurantOrder;
}
