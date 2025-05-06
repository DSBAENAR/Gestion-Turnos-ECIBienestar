package com.shiftmanagement.app_core.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.shiftmanagement.app_core.model.Shift;

public interface ShiftRepository extends MongoRepository<Shift,String> {
    long countBySpecialtyIdAndCreatedAtBetween(String specialtyId, LocalDateTime startOfDay, LocalDateTime endOfDay);

    /** 
     * Taking into account the methods of MongoRepository, we can invoke the deletion of a shift by the ID
     * @param id: the ID of the shift to delete.
     */
    void deleteById(String id);
    
    /**
     * This method searches for all shifts starting from a role
     * @param role: this role is defined in the Shift class
     * @return a list of shifts that match the role
     */
    List<Shift> findByRole(String role);

}
