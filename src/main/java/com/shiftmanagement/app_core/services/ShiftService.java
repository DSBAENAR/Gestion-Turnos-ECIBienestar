package com.shiftmanagement.app_core.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.shiftmanagement.app_core.model.Shift;
import com.shiftmanagement.app_core.model.Speciality;
import com.shiftmanagement.app_core.repository.ShiftRepository;

public class ShiftService {
    private final ShiftRepository shiftRepository;

    public ShiftService(ShiftRepository shiftRepository) {
        this.shiftRepository = shiftRepository;
    }

    public String generateCode(Shift shift){
        Speciality specialty = new Speciality();
        String prefix = specialty.getCodePrefix(); // e.g., "O"
        
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        long count = shiftRepository.countBySpecialtyIdAndCreatedAtBetween(
                specialty.getId(), startOfDay, endOfDay);

        // 3. Create the new turn code: "O-16" if 15 already exist
        long nextNumber = count + 1;
        return prefix + "-" + nextNumber;
    }
    
}


