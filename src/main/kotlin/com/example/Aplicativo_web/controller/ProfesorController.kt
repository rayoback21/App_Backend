package com.example.Aplicativo_web.controller

import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/profesor")
class ProfesorController {

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('profesor')")
    fun getDashboardInfo(): ResponseEntity<String> {
        return ResponseEntity.ok("Bienvenido Profesor. Aquí va el resumen de tus actividades.")
    }
}
