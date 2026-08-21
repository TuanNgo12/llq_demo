package com.example.demo.controller;

import com.example.demo.service.ComponentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/components")
@CrossOrigin(origins = "http://localhost:4200")
public class ComponentController {

    @Autowired
    public ComponentService componentService;

    @GetMapping("")
    public ResponseEntity<?> getAllComponents() {
        return ResponseEntity.ok(componentService.findAll());
    }

}
