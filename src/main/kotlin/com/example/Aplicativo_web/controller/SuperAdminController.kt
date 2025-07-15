package com.example.Aplicativo_web.controller

import com.example.Aplicativo_web.dto.ExamDto
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/superadmin")
class SuperAdminController {

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('super_admin')")
    fun getDashboardInfo(): ResponseEntity<String> {
        // Aquí puedes devolver estadísticas generales
        return ResponseEntity.ok("Bienvenido SuperAdmin. Estadísticas generales disponibles.")
    }

    @PostMapping("/create-exam")
    @PreAuthorize("hasRole('super_admin')")
    fun createExam(@RequestBody examDto: ExamDto): ResponseEntity<String> {
        // Aquí iría la lógica para guardar el examen en la base de datos
        return ResponseEntity.ok("Examen creado: ${examDto.title}")
    }
}
