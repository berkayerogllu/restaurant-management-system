package com.berkay.restaurant_system.mappers;

import com.berkay.restaurant_system.dtos.StaffDto;
import com.berkay.restaurant_system.entities.Staff;

public class StaffMapper {

    public static StaffDto mapToStaffDto(Staff staff) {
        return new StaffDto(staff.getId(), staff.getUsername());
    }
}
