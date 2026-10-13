package com.berkay.restaurant_system.mappers;

import com.berkay.restaurant_system.dtos.ReservationDto;
import com.berkay.restaurant_system.entities.Reservation;
import com.berkay.restaurant_system.entities.RestaurantTable;

public class ReservationMapper {

    public static ReservationDto mapToReservationDto(Reservation reservation) {
        Long tableId = (reservation.getRestaurantTable() != null) ? reservation.getRestaurantTable().getId() : null;

        return new ReservationDto(
                reservation.getId(),
                reservation.getCustomerName(),
                reservation.getCustomerPhone(),
                reservation.getStartTime(),
                reservation.getEndTime(),
                tableId
        );
    }

    public static Reservation mapToReservation(ReservationDto reservationDto) {
        Reservation reservation = new Reservation();
        reservation.setId(reservationDto.getId());
        reservation.setCustomerName(reservationDto.getCustomerName());
        reservation.setCustomerPhone(reservationDto.getCustomerPhone());
        reservation.setStartTime(reservationDto.getStartTime());
        reservation.setEndTime(reservationDto.getEndTime());

        if (reservationDto.getTableId() != null) {
            RestaurantTable table = new RestaurantTable();
            table.setId(reservationDto.getTableId());
            reservation.setRestaurantTable(table);
        }

        return reservation;
    }
}
