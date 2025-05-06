package com.shiftmanagement.app_core.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import org.springframework.stereotype.Service;

import com.shiftmanagement.app_core.model.Shift;
import com.shiftmanagement.app_core.model.ShiftStatus;
import com.shiftmanagement.app_core.repository.ShiftRepository;

@Service
public class ShiftService {
    private final ShiftRepository shiftRepository;

    public ShiftService(ShiftRepository shiftRepository) {
        this.shiftRepository = shiftRepository;
    }

    public void generateShift(Shift shift){
        String specialtyId = shift.getSpecialtyId();    
        String prefix = specialtyId;                   
    
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);
    
        long count = shiftRepository.countBySpecialtyIdAndCreatedAtBetween(
                specialtyId, startOfDay, endOfDay);
    
        long nextNumber = count + 1;
        shift.setTurnCode(prefix + "-" + nextNumber);
        shift.setStatus(ShiftStatus.IN_PROGRESS);
        shift.setCreatedAt(LocalDateTime.now());
    
        shiftRepository.insert(shift);
    }
    

    public List<Shift> getShifts(){
        return shiftRepository.findAll();
    }

    /**
     * This method first checks whether a shift with the given ID exists in the database.
     * If it does, it proceeds to delete it. If not, it throws an IllegalArgumentException.
     * 
     * We use optional since we are expecting one or no results by searching for the ID.
     * 
     * @param id the unique identifier of the shift to delete; must not be null or empty.
    */
    public void deleteShift(String id){
        Optional<Shift> shift = shiftRepository.findById(id);
        if (shift.isPresent()) {
            shiftRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("No shift found with ID: " + id);
        }
    }

    /**
     * This method searches for all shifts starting from a role
     * @param role: this role is defined in the Shift class
     * @return a list of shifts that match the role
     */
    public List<Shift> getShiftsByRole(String role) {
        List<Shift> shifts = shiftRepository.findByRole(role);
        if (shifts == null || shifts.isEmpty()) {
            throw new IllegalArgumentException("No shifts found for role: " + role);
        }
        return shifts;
    }
}


