package com.shiftmanagement.app_core.controllers;

import org.springframework.web.bind.annotation.*;
import com.shiftmanagement.app_core.model.Shift;
import com.shiftmanagement.app_core.model.ShiftStatus;
import com.shiftmanagement.app_core.services.ShiftService;

import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * REST Controller for managing shift-related operations.
 * Provides endpoints for creating, retrieving, updating, and deleting shifts.
 */

@RestController
@RequestMapping("/api/shifts")
@Tag(name = "Turnos", description = "Endpoints disponibles de los turnos")
public class ShiftController {
    private final ShiftService shiftService;

    public ShiftController(ShiftService shiftService) {
        this.shiftService = shiftService;
    }

    /**
     * Retrieves all shifts.
     *
     * @return a Flux stream of all Shift objects
     */
    @GetMapping("")
    public Flux<Shift> getAllShifts() {
        return shiftService.getShifts();
    }

    /**
     * Creates a new shift.
     *
     * @param shift the Shift object to create
     * @return a response with HTTP 201 on success, or 500 on error
     */
    @PostMapping("")
    public Mono<ResponseEntity<Shift>> postShift(@RequestBody Shift shift) {
        return shiftService.generateShift(shift)
            .map(savedShift -> ResponseEntity.status(HttpStatus.CREATED).body(savedShift))
            .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()));
    }

    /**
     * Deletes a shift by its ID.
     *
     * @param id the ID of the shift to delete
     * @return a response with HTTP 200 on success or 404 if not found
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<String>> deleteShift(@PathVariable String id) {
        return shiftService.deleteShift(id)
            .thenReturn(ResponseEntity.ok("Shift " + id + " deleted successfully."))
            .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage())));
    }

    /**
     * Retrieves all shifts by user role.
     *
     * @param role the user role (e.g., ADMIN, DOCTOR)
     * @return a Flux of Shift objects
     */
    @GetMapping("/role/{role}")
    public Flux<Shift> getShiftsByRole(@PathVariable String role) {
        return shiftService.getShiftsByRole(role);
    }

    /**
     * Retrieves a shift by its database ID.
     *
     * @param id the unique ID of the shift
     * @return a Mono containing the Shift or an error response
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<?>> getShiftById(@PathVariable String id) {
        return shiftService.getShiftById(id)
            .<ResponseEntity<?>>map(ResponseEntity::ok)
            .onErrorResume(e ->
                Mono.just(ResponseEntity.status(500)
                .body(Collections.singletonMap("error", e.getMessage())))
            );
        }   


    /**
     * Retrieves all shifts assigned to a specific user.
     *
     * @param id the user ID
     * @return a Flux of Shift objects
     */
    @GetMapping("/user/{id}")
    public Flux<Shift> getShiftsByUserId(@PathVariable String id) {
        return shiftService.getShiftsByUserId(id);
    }


    /**
     * Deletes a shift using its turn code.
     *
     * @param code the unique turn code of the shift
     * @return a response indicating success or error
     */
    @DeleteMapping("/turnCode/{code}")
    public Mono<ResponseEntity<?>> deleteTurnByCode(@PathVariable String code) {
        return shiftService.deleteShiftByTurnCode(code)
            .<ResponseEntity<?>>map(ResponseEntity::ok)
            .onErrorResume(e ->
                Mono.just(ResponseEntity.status(500)
                .body(Collections.singletonMap("error", e.getMessage())))
            );
    }


    /**
     * Retrieves a shift using its turn code.
     *
     * @param code the unique turn code
     * @return a response containing the shift or an error
     */
    @GetMapping("/shift/{code}")
    public Mono<ResponseEntity<?>> getShiftByTurnCode(@PathVariable String code) {
        return shiftService.getShiftByTurnCode(code)
            .<ResponseEntity<?>>map(ResponseEntity::ok)
            .onErrorResume(e ->
                Mono.just(ResponseEntity.status(500)
                .body(Collections.singletonMap("error", e.getMessage())))
            );
    }


    /**
     * Changes the status of a shift given its turn code.
     *
     * @param turnCode the turn code of the shift
     * @param status the new status to set
     * @return a response with the updated shift or an error
     */
    @PutMapping("/{turnCode}")
    public Mono<ResponseEntity<Object>> changeShiftStatus(@PathVariable String turnCode, @RequestBody ShiftStatus status) {
        return shiftService.getShiftByTurnCode(turnCode)
            .flatMap(shift -> shiftService.changeShiftStatus(shift, status))
            .map(updated -> ResponseEntity.<Object>ok(updated)) 
            .onErrorResume(e ->
                Mono.just(ResponseEntity.status(500)
                    .body(Collections.singletonMap("error", e.getMessage())))
            );
    }

    
    /**
     * Retrieves all shifts for a given specialty on the current day.
     *
     * @param specialty the medical specialty
     * @return a Flux of Shift objects
     */
    @GetMapping("/specialty/{specialty}")
    public Flux<Shift> getShiftsBySpecialty(@PathVariable String specialty) {
        return shiftService.getShiftsBySpecialty(specialty);
    }


    /**
     * Retrieves all shifts with a given status on the current day.
     *
     * @param status the shift status (e.g., ASSIGNED, IN_PROGRESS, ATTENDED)
     * @return a Flux of Shift objects
     */
    @GetMapping("/status/{status}")
    public Flux<Shift> getShiftsByStatus(@PathVariable ShiftStatus status) {
        return shiftService.getShiftsByStatus(status);
    }

    /**
     * Retrieves all shifts with or without special priority on the current day.
     *
     * @param specialPriority true if the shift has special priority
     * @return a Flux of Shift objects
     */
    @GetMapping("/priority/{priority}")
    public Flux<Shift> getShiftsBySpecialPriority(@PathVariable boolean specialPriority) {
        return shiftService.getShiftsBySpecialPriority(specialPriority);
    }


}
