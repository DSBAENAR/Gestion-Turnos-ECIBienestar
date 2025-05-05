package com.shiftmanagement.app_core.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shiftmanagement.app_core.model.Shift;
import com.shiftmanagement.app_core.services.ShiftService;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/shifts")
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
    
    
}
