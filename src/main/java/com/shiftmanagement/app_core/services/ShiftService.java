package com.shiftmanagement.app_core.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.stereotype.Service;

import com.shiftmanagement.app_core.model.Prefix;
import com.shiftmanagement.app_core.model.Shift;
import com.shiftmanagement.app_core.model.ShiftStatus;
import com.shiftmanagement.app_core.repository.ShiftRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ShiftService {

    private final ShiftRepository shiftRepository;
    private final UserService userService;
    LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
    LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

    public ShiftService(ShiftRepository shiftRepository, UserService userService) {
        this.shiftRepository = shiftRepository;
        this.userService = userService;
    }

    /**
     * Generates a new shift with a turn code and assigned status.
     *
     * @param shift the shift object to be created
     * @return a Mono that completes when the shift is saved
     */
    public Mono<Void> generateShift(Shift shift) {
        String specialty = shift.getSpecialty();
        Prefix prefix = switch (specialty) {
            case "Psicologia" -> Prefix.PS;
            case "Medicina General" -> Prefix.MG;
            default -> Prefix.OD;
        };

        return userService.getUserbyId(shift.getUserId())
            .flatMap(user -> shiftRepository.countBySpecialtyAndCreatedAtBetween(specialty, startOfDay, endOfDay)
                .map(count -> {
                    long nextNumber = count + 1;
                    shift.setTurnCode(prefix + "-" + nextNumber);
                    shift.setStatus(ShiftStatus.ASSIGNED);
                    shift.setCreatedAt(LocalDateTime.now());
                    shift.setUserId(user.numberId());
                    shift.setUsername(user.userName());
                    shift.setUserRole(user.role());
                    return shift;
                }))
            .flatMap(shiftRepository::insert)
            .then(); 
    }

    /**
     * Retrieves all shifts from the database.
     *
     * @return a Flux of all Shift objects
     */
    public Flux<Shift> getShifts() {
        return shiftRepository.findAll();
    }

     /**
     * Deletes a shift by its unique ID.
     *
     * @param id the ID of the shift to delete
     * @return a Mono that completes when deletion is successful
     */
    public Mono<Void> deleteShift(String id) {
        return shiftRepository.findById(id)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("No shift found with ID: " + id)))
            .flatMap(existing -> shiftRepository.deleteById(id));
    }

    /**
     * Retrieves all shifts associated with a specific user role.
     *
     * @param role the user role to filter by
     * @return a Flux of Shift objects with the specified role
     */
    public Flux<Shift> getShiftsByRole(String role) {
        return shiftRepository.findByUserRole(role)
            .switchIfEmpty(Flux.error(new IllegalArgumentException("No shifts found for role: " + role)));
    }

    /**
     * Retrieves all shifts assigned to a specific user.
     *
     * @param userId the user ID to filter by
     * @return a Flux of Shift objects for the user
     */
    public Flux<Shift> getShiftsByUserId(String userId) {
        return shiftRepository.findByUserId(userId)
            .switchIfEmpty(Flux.error(new IllegalArgumentException("No shifts found for user: " + userId)));
    }

    /**
     * Finds a shift by its turn code and current date.
     *
     * @param code the turn code of the shift
     * @return a Mono of the Shift object if found
     */
    public Mono<Shift> getShiftByTurnCode(String code) {
        return shiftRepository.findByTurnCodeAndCreatedAtBetween(code, startOfDay, endOfDay)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("No shift found with code: " + code)));
    }

    /**
     * Deletes a shift based on its turn code and current date.
     *
     * @param turnCode the turn code of the shift to delete
     * @return a Mono with the deleted turn code
     */
    public Mono<String> deleteShiftByTurnCode(String turnCode) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);
        return shiftRepository.findByTurnCodeAndCreatedAtBetween(turnCode, startOfDay, endOfDay)
            .switchIfEmpty(Mono.error(new RuntimeException("No shift found with turnCode: " + turnCode)))
            .flatMap(shift -> shiftRepository.delete(shift).thenReturn(turnCode));
    }


    /**
     * Retrieves a shift by its database ID.
     *
     * @param id the ID of the shift
     * @return a Mono of the Shift object
     */
    public Mono<Shift> getShiftById(String id) {
        return shiftRepository.findById(id)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("No shift found with ID: " + id)));
    }


    /**
     * Changes the status of a shift if the transition is valid.
     *
     * @param shift the shift to update
     * @param newStatus the new status to assign
     * @return a Mono of the updated Shift
     */
    public Mono<Shift> changeShiftStatus(Shift shift, ShiftStatus newStatus) {
        if (newStatus == null) {
            return Mono.error(new IllegalArgumentException("The new status cannot be null"));
        }

        ShiftStatus currentStatus = shift.getStatus();
        if (!isValidTransition(currentStatus, newStatus)) {
            return Mono.error(new IllegalStateException("Transition not allowed from " + currentStatus + " to " + newStatus));
        }

        shift.setStatus(newStatus);
        return shiftRepository.save(shift);
    }


    /**
     * Validates status transitions for shifts.
     *
     * @param from current status
     * @param to desired status
     * @return true if the transition is allowed, false otherwise
     */
    private boolean isValidTransition(ShiftStatus from, ShiftStatus to) {
        return switch (from) {
            case ASSIGNED -> to == ShiftStatus.IN_PROGRESS || to == ShiftStatus.CANCELED;
            case IN_PROGRESS -> to == ShiftStatus.ATTENDED || to == ShiftStatus.CANCELED;
            case ATTENDED, CANCELED -> false;
        };
    }


    /**
     * Retrieves all shifts for a given specialty on the current day.
     *
     * @param specialty the medical specialty to filter by
     * @return a Flux of Shift objects
     */
    public Flux<Shift> getShiftsBySpecialty(String specialty) {
        return shiftRepository.findBySpecialtyAndCreatedAtBetween(specialty, startOfDay, endOfDay)
            .switchIfEmpty(Flux.error(new IllegalArgumentException("No shifts found for specialty: " + specialty)));
        }

    /**
     * Retrieves all shifts with a specific status on the current day.
     *
     * @param status the status to filter by
     * @return a Flux of Shift objects
     */
    public Flux<Shift> getShiftsByStatus(ShiftStatus status) {
        return shiftRepository.findByStatusAndCreatedAtBetween(status, startOfDay, endOfDay)
            .switchIfEmpty(Flux.error(new IllegalArgumentException("No shifts found with status: " + status)));
        }

    /**
     * Retrieves all shifts with or without special priority on the current day.
     *
     * @param specialPriority whether the shift is marked as special priority
     * @return a Flux of Shift objects
     */
    public Flux<Shift> getShiftsBySpecialPriority(boolean specialPriority) {
        return shiftRepository.findBySpecialPriorityAndCreatedAtBetween(specialPriority, startOfDay, endOfDay)
        .switchIfEmpty(Flux.error(new IllegalArgumentException("No shifts found with specialPriority: " + specialPriority)));
        }
}
