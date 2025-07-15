package com.example.Aplicativo_web.controller

import com.example.Aplicativo_web.entity.Results
import com.example.Aplicativo_web.service.ResultsService
import com.example.Aplicativo_web.service.UsersAdminService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/results")
class ResultsController(
    private val resultsService: ResultsService,
    private val usersAdminService: UsersAdminService // para buscar usuario
) {

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

    // Nuevo endpoint para que el profesor vea sus resultados filtrados
    @GetMapping("/my")
    @PreAuthorize("hasRole('profesor')")
    fun getResultsForLoggedProfessor(): ResponseEntity<List<Results>> {
        val authentication = SecurityContextHolder.getContext().authentication
        val principal = authentication.principal

        val username = when (principal) {
            is UserDetails -> principal.username
            is String -> principal
            else -> return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }

        val professorId = usersAdminService.findUserIdByUsername(username)

        val results = resultsService.getResultsByProfessorId(professorId)

        return ResponseEntity.ok(results)
    }

    // Nuevo endpoint para que el aspirante vea su resultado personal
    @GetMapping("/me")
    @PreAuthorize("hasRole('aspirante')")
    fun getResultsForAspirant(): ResponseEntity<Results> {
        val authentication = SecurityContextHolder.getContext().authentication
        val principal = authentication.principal

        val username = when (principal) {
            is UserDetails -> principal.username
            is String -> principal
            else -> return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }

        val aspirantId = usersAdminService.findUserIdByUsername(username)

        val result = resultsService.findAll().find { it.aspirant?.id == aspirantId }

        return if (result != null) ResponseEntity.ok(result)
        else ResponseEntity.notFound().build()
    }
}
