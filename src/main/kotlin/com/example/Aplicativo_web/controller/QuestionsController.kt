package com.example.Aplicativo_web.controller

import com.example.Aplicativo_web.entity.Questions
import com.example.Aplicativo_web.service.QuestionsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize // Importar para seguridad
import org.springframework.web.bind.annotation.*
// import jakarta.validation.Valid // No es necesario si no usas DTOs con @Valid

@RestController
@RequestMapping("/questions")
@CrossOrigin(methods = [RequestMethod.GET, RequestMethod.POST, RequestMethod.PATCH, RequestMethod.PUT, RequestMethod.DELETE])
class QuestionsController {

    @Autowired
    private lateinit var questionsService: QuestionsService

    // --- GET (Leer) ---
    // Cualquier usuario autenticado puede listar todas las preguntas
    @GetMapping
    @PreAuthorize("hasAnyRole('admin')")
    fun list(): ResponseEntity<List<Questions>> {
        return ResponseEntity(questionsService.list(), HttpStatus.OK)
    }

    // Cualquier usuario autenticado puede obtener una pregunta por ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('admin')")
    fun getById(@PathVariable id: Long): ResponseEntity<Questions> {
        return ResponseEntity(questionsService.getQuestionById(id), HttpStatus.OK)
    }

    // Admin puede obtener preguntas por ID de Aspirante
    @GetMapping("/by-aspirant/{aspirantId}")
    @PreAuthorize("hasRole('admin')")
    fun getByAspirantId(@PathVariable aspirantId: Long): ResponseEntity<List<Questions>> {
        return ResponseEntity(questionsService.getQuestionsByAspirantId(aspirantId), HttpStatus.OK)
    }

    // Admin puede obtener preguntas por ID de Admin
    @GetMapping("/by-admin/{adminId}")
    @PreAuthorize("hasRole('admin')")
    fun getByAdminId(@PathVariable adminId: Long): ResponseEntity<List<Questions>> {
        return ResponseEntity(questionsService.getQuestionsByAdminId(adminId), HttpStatus.OK)
    }

    // --- POST (Crear) ---
    // Solo un ADMIN puede crear una pregunta
    // El cuerpo de la petición debe ser un objeto Questions completo, incluyendo { "aspirants": { "id": 1 } }
    @PostMapping
    @PreAuthorize("hasRole('admin')")
    fun save(@RequestBody questions: Questions): ResponseEntity<Questions> {
        return ResponseEntity(questionsService.save(questions), HttpStatus.CREATED)
    }

    // --- PUT (Actualizar completamente) ---
    // Solo un ADMIN puede actualizar una pregunta por su ID
    // El cliente debe enviar el ID en la URL y la entidad Questions completa en el cuerpo,
    // donde questions.id debe coincidir con el {id} del PathVariable.
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    fun update(@PathVariable id: Long, @RequestBody questions: Questions): ResponseEntity<Questions> {
        // Asegurarse de que el ID en la ruta coincide con el ID en el cuerpo
        if (questions.id == null || questions.id != id) {
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }
        return ResponseEntity(questionsService.update(questions), HttpStatus.OK)
    }

    // --- PATCH (Actualizar parcialmente - solo texto) ---
    // Solo un ADMIN puede actualizar el texto de una pregunta por su ID
    // El cuerpo de la petición podría ser un JSON como: { "text": "Nuevo texto de la pregunta" }
    // Nota: Para este caso sin DTOs, es un poco más manual.
    // Podrías hacer un @RequestBody Map<String, Any> o crear un micro DTO interno solo para este método.
    // O, más simple, que el cliente solo envíe la cadena de texto si solo es un campo.
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    fun updateText(@PathVariable id: Long, @RequestBody requestBody: Map<String, String>): ResponseEntity<Questions> {
        val newText = requestBody["text"]
        if (newText == null || newText.isBlank()) {
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }
        return ResponseEntity(questionsService.updateText(id, newText), HttpStatus.OK)
    }


    // --- DELETE (Eliminar) ---
    // Solo un ADMIN puede eliminar una pregunta por su ID
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    fun delete(@PathVariable id: Long): ResponseEntity<Unit> { // Unit para ResponseEntity sin cuerpo
        questionsService.delete(id)
        return ResponseEntity(HttpStatus.NO_CONTENT) // 204 No Content
    }
}