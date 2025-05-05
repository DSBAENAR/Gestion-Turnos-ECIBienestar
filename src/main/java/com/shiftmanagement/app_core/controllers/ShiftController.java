package com.shiftmanagement.app_core.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;



@RestController
@RequestMapping("/api/shifts")
public class ShiftController {
    @GetMapping("")
    public String getMethodName() {
        return "Hola Mundo";
    }
    
}
