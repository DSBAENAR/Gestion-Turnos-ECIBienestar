package com.shiftmanagement.app_core.repository;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.shiftmanagement.app_core.model.Shift;

public interface ShiftRepository extends MongoRepository<Shift,String> {
    long countBySpecialtyIdAndCreatedAtBetween(String specialtyId, LocalDateTime startOfDay, LocalDateTime endOfDay);
    
}
