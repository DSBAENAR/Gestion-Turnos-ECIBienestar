package com.shiftmanagement.app_core.repository;

import java.time.LocalDateTime;



import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.shiftmanagement.app_core.model.Shift;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ShiftRepository extends ReactiveMongoRepository<Shift,String> {
    Mono<Long> countBySpecialtyAndCreatedAtBetween(String specialty, LocalDateTime startOfDay, LocalDateTime endOfDay);

    /** 
     * Taking into account the methods of MongoRepository, we can invoke the deletion of a shift by the ID
     * @param id: the ID of the shift to delete.
     * @return 
     */
    Mono<Void> deleteById(String id);
    
    /**
     * This method searches for all shifts starting from a role
     * @param role: this role is defined in the Shift class
     * @return a list of shifts that match the role
     */
    Flux<Shift> findByUserRole(String role);

     /**
     * This method searches for all shifts starting from a role
     * @param role: this role is defined in the Shift class
     * @return a list of shifts that match the role
     */
    Flux<Shift> findByUserId(String userId);

    Mono<Shift> findByTurnCodeAndCreatedAtBetween(String code, LocalDateTime startOfDay, LocalDateTime endOfDay);
}
