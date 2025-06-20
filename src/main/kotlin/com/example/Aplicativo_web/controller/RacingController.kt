package com.example.Aplicativo_web.controller

import com.example.Aplicativo_web.entity.Racing
import com.example.Aplicativo_web.service.RacingService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/racing")
class RacingController(private val racingService: RacingService) {

    @GetMapping
    fun getAll(): List<Racing> = racingService.findAll()

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<Racing> =
        racingService.findById(id)?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()

    @PostMapping
    fun create(@RequestBody racing: Racing): Racing = racingService.save(racing)

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody racing: Racing): ResponseEntity<Racing> {
        val existing = racingService.findById(id) ?: return ResponseEntity.notFound().build()
        existing.career = racing.career
        existing.aspirants = racing.aspirants
        existing.admins = racing.admins
        return ResponseEntity.ok(racingService.save(existing))
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        val existing = racingService.findById(id) ?: return ResponseEntity.notFound().build()
        racingService.deleteById(existing.id!!)
        return ResponseEntity.noContent().build()
    }
}
