package com.shiftmanagement.app_core.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shiftmanagement.app_core.model.Shift;
import com.shiftmanagement.app_core.services.ShiftService;

import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/shifts")
@CrossOrigin("http://localhost:3000")
public class ShiftController {
    private final ShiftService shiftService;
    
    public ShiftController(ShiftService shiftService) {
        this.shiftService = shiftService;
    }

    @GetMapping("")
    public List<Shift> getAllShifts() {
        return shiftService.getShifts();
    }

    @PostMapping("")
    public void postShift(@RequestBody Shift shift) {
        shiftService.generateShift(shift);
    }

    /**
     * This endpoint deletes a shift identified by its unique ID. This method uses the ShiftService to perform the operation 
     * and handles errors if the shift doesn't exist.
     * @param id the unique identifier of the shift to delete; must not be null or empty.
     * 
     * We get responses of type 200 if the action was completed or 404 if it was not found.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteShift(@PathVariable String id) {
        try {
            shiftService.deleteShift(id);
            return ResponseEntity.ok("Shift deleted successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * This endpoint allows you to retrieve a list of shifts filtered by the role field. If no shifts are found with that role, 
     * a 404 Not Found response is returned.
     * @param role the role to filter shifts by; must not be null or empty.
     */
    @GetMapping("/role/{role}")
    public ResponseEntity<?> getShiftsByRole(@PathVariable String role) {
        try {
            List<Shift> shifts = shiftService.getShiftsByRole(role);
            if (shifts.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No shifts found for role: " + role);
            }
            return ResponseEntity.ok(shifts);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("No shifts found for role: " + role);
        }
    }

    @GetMapping("/{id}")
     public ResponseEntity<?> getShiftById(@PathVariable String id){
         try{
             Shift shift = shiftService.getShiftById(id);
             return ResponseEntity.status(200).body(shift);
         }
         catch (RuntimeException e){
             return ResponseEntity.status(500).body(Collections.singletonMap("error", e.getMessage()));
         }
     }

    @GetMapping("/user/{id}")
    public ResponseEntity<?> getShiftsByUserId(@PathVariable String id) {
        try {
            List<Shift> shifts = shiftService.getShiftsByUserId(id);
            if (shifts.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No shifts found for id: " + id);
            }
            return ResponseEntity.ok(shifts);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("No shifts found for id: " + id);
        }
    }

    @DeleteMapping("/{code}")
     public ResponseEntity<?> deleteTurnByCode(@PathVariable String code) {
         try {
             String turnForDelete = shiftService.deleteShiftByTurnCode(code);
             return ResponseEntity.status(200).body(Collections.singletonMap("response", "turn: " + turnForDelete + " Delete OK"));
        } catch (RuntimeException e) {
             return ResponseEntity.status(500).body(Collections.singletonMap("error", e.getMessage()));
        }
    }

     @GetMapping("/{code}")
     public ResponseEntity<?> getShiftByTurnCode(@PathVariable String code){
         try{
             Shift shift = shiftService.getShiftByTurnCode(code);
             return ResponseEntity.status(200).body(shift);
        }
         catch (RuntimeException e){
             return ResponseEntity.status(500).body(Collections.singletonMap("error", e.getMessage()));
        }
     }
}
