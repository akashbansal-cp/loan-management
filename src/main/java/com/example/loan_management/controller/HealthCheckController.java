package com.example.loan_management.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;

@RestController
public class HealthCheckController {

    @GetMapping("/healthCheck")
    ResponseEntity<String> getHeathCheck(){
        return new ResponseEntity<>("true", OK);
    }
}
