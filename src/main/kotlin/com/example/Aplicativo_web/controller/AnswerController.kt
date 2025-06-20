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
    // Cualquier usuario autenticado puede listar todas las respuestas
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ASPIRANT')")
    fun list(): ResponseEntity<List<Answer>> {
        return ResponseEntity(answerService.list(), HttpStatus.OK)
    }

    // Cualquier usuario autenticado puede obtener una respuesta por ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ASPIRANT')")
    fun getById(@PathVariable id: Long): ResponseEntity<Answer> {
        return ResponseEntity(answerService.getAnswerById(id), HttpStatus.OK)
    }

    // Obtener respuestas por ID de Aspirante (útil para ver todas las respuestas de un aspirante)
    @GetMapping("/by-aspirant/{aspirantId}")
    @PreAuthorize("hasAnyRole('admin')") // Puede que un aspirante solo pueda ver las suyas
    fun getByAspirantId(@PathVariable aspirantId: Long): ResponseEntity<List<Answer>> {
        // CONSIDERACIÓN DE SEGURIDAD: Aquí podrías añadir lógica para que un ASPIRANT solo pueda
        // ver sus propias respuestas, verificando el ID del usuario autenticado.
        return ResponseEntity(answerService.getAnswersByAspirantId(aspirantId), HttpStatus.OK)
    }

    // Obtener respuestas por ID de Pregunta (útil para ver todas las respuestas a una pregunta específica)
    @GetMapping("/by-question/{questionId}")
    @PreAuthorize("hasAnyRole('admin')") // Permisos según tu lógica de negocio
    fun getByQuestionId(@PathVariable questionId: Long): ResponseEntity<List<Answer>> {
        return ResponseEntity(answerService.getAnswersByQuestionId(questionId), HttpStatus.OK)
    }

    // Obtener una respuesta específica de un aspirante a una pregunta
    @GetMapping("/by-aspirant/{aspirantId}/by-question/{questionId}")
    @PreAuthorize("hasAnyRole('admin')")
    fun getByAspirantAndQuestion(
        @PathVariable aspirantId: Long,
        @PathVariable questionId: Long
    ): ResponseEntity<Answer> {
        // CONSIDERACIÓN DE SEGURIDAD: Similar al anterior, un aspirante solo debería poder
        // ver sus propias respuestas.
        return ResponseEntity(answerService.getAnswerByAspirantAndQuestion(aspirantId, questionId), HttpStatus.OK)
    }

    // --- POST (Crear) ---
    // Un aspirante debería poder crear respuestas (responder a preguntas)
    @PostMapping
    @PreAuthorize("hasAnyRole('admin')") // Ambos roles pueden crear respuestas
    fun save(@RequestBody answer: Answer): ResponseEntity<Answer> {
        // CONSIDERACIÓN DE SEGURIDAD: Si es un ASPIRANT, asegúrate de que el 'aspirant.id'
        // que envía en el JSON coincide con el ID del usuario autenticado.
        // Esto previene que un aspirante envíe respuestas a nombre de otro.
        // val authenticatedUserId = SecurityContextHolder.getContext().authentication.principal.id // Asumiendo que tu Principal tiene un ID
        // if (answer.aspirant?.id != authenticatedUserId && hasRole('ASPIRANT')) { throw ... }
        return ResponseEntity(answerService.save(answer), HttpStatus.CREATED)
    }

    // --- PUT (Actualizar completamente) ---
    // Un aspirante podría actualizar su propia respuesta (si permitido) o un admin.
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('admin')")
    fun update(@PathVariable id: Long, @RequestBody answer: Answer): ResponseEntity<Answer> {
        if (answer.id == null || answer.id != id) {
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }
        // CONSIDERACIÓN DE SEGURIDAD: Si es un ASPIRANT, asegúrate de que solo puede
        // actualizar sus PROPIAS respuestas.
        val updatedAnswer = answerService.update(answer)
        return ResponseEntity(updatedAnswer, HttpStatus.OK)
    }

    // --- DELETE (Eliminar) ---
    // Solo un ADMIN puede eliminar respuestas (o quizás un aspirante solo las suyas, con más lógica)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    fun delete(@PathVariable id: Long): ResponseEntity<Unit> {
        answerService.delete(id)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
}