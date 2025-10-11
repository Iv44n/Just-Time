package com.justtimeapi.api.controllers;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/admin")
@RequiredArgsConstructor
public class AdminController {

    @GetMapping("/dashboard")
    public String dashboard() {
        return "Bienvenido ADMIN";
    }
}
