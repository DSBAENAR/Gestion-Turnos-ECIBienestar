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

@RestController
@RequestMapping("/api/shifts")
@Tag(name = "Turnos", description = "Endpoints disponibles de los turnos")
public class ShiftController {
    private final ShiftService shiftService;

    public ShiftController(ShiftService shiftService) {
        this.shiftService = shiftService;
    }

    @GetMapping("")
    public Flux<Shift> getAllShifts() {
        return shiftService.getShifts();
    }

    @PostMapping("")
    public Mono<ResponseEntity<String>> postShift(@RequestBody Shift shift) {
        return shiftService.generateShift(shift)
            .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body("Turno generado correctamente"))
            .onErrorResume(e -> Mono.just(ResponseEntity.status(500).body(e.getMessage())));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<String>> deleteShift(@PathVariable String id) {
        return shiftService.deleteShift(id)
            .thenReturn(ResponseEntity.ok("Shift " + id + " deleted successfully."))
            .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage())));
    }

    @GetMapping("/role/{role}")
    public Flux<Shift> getShiftsByRole(@PathVariable String role) {
        return shiftService.getShiftsByRole(role);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<?>> getShiftById(@PathVariable String id) {
        return shiftService.getShiftById(id)
            .<ResponseEntity<?>>map(ResponseEntity::ok)
            .onErrorResume(e ->
                Mono.just(ResponseEntity.status(500)
                .body(Collections.singletonMap("error", e.getMessage())))
            );
        }   



    @GetMapping("/user/{id}")
    public Flux<Shift> getShiftsByUserId(@PathVariable String id) {
        return shiftService.getShiftsByUserId(id);
    }

    @DeleteMapping("/turnCode/{code}")
    public Mono<ResponseEntity<?>> deleteTurnByCode(@PathVariable String code) {
        return shiftService.deleteShiftByTurnCode(code)
            .<ResponseEntity<?>>map(ResponseEntity::ok)
            .onErrorResume(e ->
                Mono.just(ResponseEntity.status(500)
                .body(Collections.singletonMap("error", e.getMessage())))
            );
    }

    @GetMapping("/shift/{code}")
    public Mono<ResponseEntity<?>> getShiftByTurnCode(@PathVariable String code) {
        return shiftService.getShiftByTurnCode(code)
            .<ResponseEntity<?>>map(ResponseEntity::ok)
            .onErrorResume(e ->
                Mono.just(ResponseEntity.status(500)
                .body(Collections.singletonMap("error", e.getMessage())))
            );
    }

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


}
