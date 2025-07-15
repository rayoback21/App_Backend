package com.example.Aplicativo_web.controller

import com.example.Aplicativo_web.entity.Answer
import com.example.Aplicativo_web.service.AnswerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/answers")
@CrossOrigin(methods = [RequestMethod.GET, RequestMethod.POST, RequestMethod.PATCH, RequestMethod.PUT, RequestMethod.DELETE])
class AnswerController {

    @Autowired
    private lateinit var answerService: AnswerService

    // --- GET (Leer) ---
    // superadmin, profesor y aspirante pueden listar respuestas
    @GetMapping
    @PreAuthorize("hasAnyRole('superadmin', 'profesor', 'aspirante')")
    fun list(): ResponseEntity<List<Answer>> {
        return ResponseEntity(answerService.list(), HttpStatus.OK)
    }

    // Obtener respuesta por id (superadmin, profesor y aspirante)
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('superadmin', 'profesor', 'aspirante')")
    fun getById(@PathVariable id: Long): ResponseEntity<Answer> {
        return ResponseEntity(answerService.getAnswerById(id), HttpStatus.OK)
    }

    // Obtener respuestas por aspirante (superadmin y profesor)
    @GetMapping("/by-aspirant/{aspirantId}")
    @PreAuthorize("hasAnyRole('superadmin', 'profesor')")
    fun getByAspirantId(@PathVariable aspirantId: Long): ResponseEntity<List<Answer>> {
        return ResponseEntity(answerService.getAnswersByAspirantId(aspirantId), HttpStatus.OK)
    }

    // Obtener respuestas por pregunta (superadmin y profesor)
    @GetMapping("/by-question/{questionId}")
    @PreAuthorize("hasAnyRole('superadmin', 'profesor')")
    fun getByQuestionId(@PathVariable questionId: Long): ResponseEntity<List<Answer>> {
        return ResponseEntity(answerService.getAnswersByQuestionId(questionId), HttpStatus.OK)
    }

    // Obtener respuesta específica por aspirante y pregunta (superadmin y profesor)
    @GetMapping("/by-aspirant/{aspirantId}/by-question/{questionId}")
    @PreAuthorize("hasAnyRole('superadmin', 'profesor')")
    fun getByAspirantAndQuestion(
        @PathVariable aspirantId: Long,
        @PathVariable questionId: Long
    ): ResponseEntity<Answer> {
        return ResponseEntity(answerService.getAnswerByAspirantAndQuestion(aspirantId, questionId), HttpStatus.OK)
    }

    // --- POST (Crear) ---
    // superadmin y profesor pueden crear respuestas
    @PostMapping
    @PreAuthorize("hasAnyRole('superadmin', 'profesor')")
    fun save(@RequestBody answer: Answer): ResponseEntity<Answer> {
        return ResponseEntity(answerService.save(answer), HttpStatus.CREATED)
    }

    // --- PUT (Actualizar) ---
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('superadmin', 'profesor')")
    fun update(@PathVariable id: Long, @RequestBody answer: Answer): ResponseEntity<Answer> {
        if (answer.id == null || answer.id != id) {
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }
        val updatedAnswer = answerService.update(answer)
        return ResponseEntity(updatedAnswer, HttpStatus.OK)
    }

    // --- DELETE (Eliminar) ---
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('superadmin', 'profesor')")
    fun delete(@PathVariable id: Long): ResponseEntity<Unit> {
        answerService.delete(id)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
}
