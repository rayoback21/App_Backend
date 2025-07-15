package com.example.Aplicativo_web.controller

import com.example.Aplicativo_web.entity.Racing
import com.example.Aplicativo_web.service.RacingService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/public/racings")
@CrossOrigin(origins = ["http://localhost:5173"])
class RacingPublicController(
    private val racingService: RacingService
) {

    @GetMapping
    fun list(): ResponseEntity<List<Racing>> {
        return ResponseEntity.ok(racingService.findAll())
    }
}
