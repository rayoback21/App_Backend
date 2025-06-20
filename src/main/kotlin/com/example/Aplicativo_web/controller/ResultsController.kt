package com.example.Aplicativo_web.controller

import com.example.Aplicativo_web.entity.Results
import com.example.Aplicativo_web.service.ResultsService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/results")
class ResultsController(private val resultsService: ResultsService) {

    @GetMapping
    fun getAll(): List<Results> = resultsService.findAll()

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<Results> =
        resultsService.findById(id)?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()

    @PostMapping
    fun create(@RequestBody results: Results): Results = resultsService.save(results)

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody results: Results): ResponseEntity<Results> {
        val existing = resultsService.findById(id) ?: return ResponseEntity.notFound().build()
        // Actualizamos campos mutable - aquí debes definir qué campos actualizar
        existing.score = results.score
        existing.examDate = results.examDate
        existing.correctAnswersCount = results.correctAnswersCount
        existing.incorrectAnswersCount = results.incorrectAnswersCount
        existing.aspirant = results.aspirant
        return ResponseEntity.ok(resultsService.save(existing))
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        val existing = resultsService.findById(id) ?: return ResponseEntity.notFound().build()
        resultsService.deleteById(existing.id!!)
        return ResponseEntity.noContent().build()
    }
}
