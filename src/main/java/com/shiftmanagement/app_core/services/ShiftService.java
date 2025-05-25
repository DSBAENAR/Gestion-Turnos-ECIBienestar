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

    public ShiftService(ShiftRepository shiftRepository, UserService userService) {
        this.shiftRepository = shiftRepository;
        this.userService = userService;
    }

    public Mono<Void> generateShift(Shift shift) {
        String specialty = shift.getSpecialty();
        Prefix prefix = switch (specialty) {
            case "Psicologia" -> Prefix.PS;
            case "Medicina General" -> Prefix.MG;
            default -> Prefix.OD;
        };

        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

        return userService.getUserbyId(shift.getUserId())
            .flatMap(user -> shiftRepository.countBySpecialtyAndCreatedAtBetween(specialty, startOfDay, endOfDay)
                .map(count -> {
                    long nextNumber = count + 1;
                    shift.setTurnCode(prefix + "-" + nextNumber);
                    shift.setStatus(ShiftStatus.ASSIGNED);
                    shift.setCreatedAt(LocalDateTime.now());
                    shift.setUserId(user.id());
                    shift.setUsername(user.userName());
                    shift.setUserRole(user.role());
                    return shift;
                }))
            .flatMap(shiftRepository::insert)
            .then(); 
    }

    public Flux<Shift> getShifts() {
        return shiftRepository.findAll();
    }

    public Mono<Void> deleteShift(String id) {
        return shiftRepository.findById(id)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("No shift found with ID: " + id)))
            .flatMap(existing -> shiftRepository.deleteById(id));
    }

    public Flux<Shift> getShiftsByRole(String role) {
        return shiftRepository.findByUserRole(role)
            .switchIfEmpty(Flux.error(new IllegalArgumentException("No shifts found for role: " + role)));
    }

    public Flux<Shift> getShiftsByUserId(String userId) {
        return shiftRepository.findByUserId(userId)
            .switchIfEmpty(Flux.error(new IllegalArgumentException("No shifts found for user: " + userId)));
    }

    public Mono<Shift> getShiftByTurnCode(String code) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);
        return shiftRepository.findByTurnCodeAndCreatedAtBetween(code, startOfDay, endOfDay)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("No shift found with code: " + code)));
    }

    public Mono<String> deleteShiftByTurnCode(String turnCode) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);
        return shiftRepository.findByTurnCodeAndCreatedAtBetween(turnCode, startOfDay, endOfDay)
            .switchIfEmpty(Mono.error(new RuntimeException("No shift found with turnCode: " + turnCode)))
            .flatMap(shift -> shiftRepository.delete(shift).thenReturn(turnCode));
    }

    public Mono<Shift> getShiftById(String id) {
        return shiftRepository.findById(id)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("No shift found with ID: " + id)));
    }

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

    private boolean isValidTransition(ShiftStatus from, ShiftStatus to) {
        return switch (from) {
            case ASSIGNED -> to == ShiftStatus.IN_PROGRESS || to == ShiftStatus.CANCELED;
            case IN_PROGRESS -> to == ShiftStatus.ATTENDED || to == ShiftStatus.CANCELED;
            case ATTENDED, CANCELED -> false;
        };
    }
}
