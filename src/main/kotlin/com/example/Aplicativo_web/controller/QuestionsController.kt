package com.example.Aplicativo_web.controller

import com.example.Aplicativo_web.entity.Questions
import com.example.Aplicativo_web.service.QuestionsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/questions")
@CrossOrigin(methods = [RequestMethod.GET, RequestMethod.POST, RequestMethod.PATCH, RequestMethod.PUT, RequestMethod.DELETE])
class QuestionsController {

    @Autowired
    private lateinit var questionsService: QuestionsService

    @GetMapping
    @PreAuthorize("hasAnyRole('profesor')")
    fun list(): ResponseEntity<List<Questions>> {
        return ResponseEntity(questionsService.list(), HttpStatus.OK)
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('profesor')")
    fun getById(@PathVariable id: Long): ResponseEntity<Questions> {
        return ResponseEntity(questionsService.getQuestionById(id), HttpStatus.OK)
    }

    @GetMapping("/by-aspirant/{aspirantId}")
    @PreAuthorize("hasRole('profesor')")
    fun getByAspirantId(@PathVariable aspirantId: Long): ResponseEntity<List<Questions>> {
        return ResponseEntity(questionsService.getQuestionsByAspirantId(aspirantId), HttpStatus.OK)
    }

    @GetMapping("/by-professor/{professorId}")
    @PreAuthorize("hasRole('profesor')")
    fun getByProfessorId(@PathVariable professorId: Long): ResponseEntity<List<Questions>> {
        return ResponseEntity(questionsService.getQuestionsByProfessorId(professorId), HttpStatus.OK)
    }

    @PostMapping("/general")
    @PreAuthorize("hasAnyRole('super_admin', 'admin')")
    fun createGeneral(@RequestBody questions: Questions): ResponseEntity<Questions> {
        return ResponseEntity(questionsService.saveGeneral(questions), HttpStatus.CREATED)
    }

    @PostMapping("/specific")
    @PreAuthorize("hasRole('profesor')")
    fun createSpecific(@RequestBody questions: Questions): ResponseEntity<Questions> {
        val authentication = SecurityContextHolder.getContext().authentication
        val principal = authentication.principal
        val username = when (principal) {
            is UserDetails -> principal.username
            is String -> principal
            else -> return ResponseEntity(HttpStatus.UNAUTHORIZED)
        }

        println("Recibida pregunta: $questions")
        println("Username autenticado: $username")

        val professor = questionsService.findProfessorByUsername(username)
            ?: return ResponseEntity(HttpStatus.UNAUTHORIZED)

        println("Profesor encontrado: ${professor.id}")

        if (questions.racing == null || questions.racing?.id == null) {
            println("Error: La carrera es requerida")
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }

        val racing = questionsService.findRacingById(questions.racing!!.id!!)
        if (racing == null) {
            println("Error: Carrera no encontrada")
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }

        if (racing.professor?.id != professor.id) {
            println("Error: Profesor no autorizado para esa carrera")
            return ResponseEntity(HttpStatus.UNAUTHORIZED)
        }

        questions.professor = professor

        println("Todo ok, guardando pregunta...")

        return ResponseEntity(questionsService.saveSpecific(questions), HttpStatus.CREATED)
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('profesor')")
    fun update(@PathVariable id: Long, @RequestBody questions: Questions): ResponseEntity<Questions> {
        if (questions.id == null || questions.id != id) {
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }
        return ResponseEntity(questionsService.update(questions), HttpStatus.OK)
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('profesor')")
    fun updateText(@PathVariable id: Long, @RequestBody requestBody: Map<String, String>): ResponseEntity<Questions> {
        val newText = requestBody["text"]
        if (newText.isNullOrBlank()) {
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }
        return ResponseEntity(questionsService.updateText(id, newText), HttpStatus.OK)
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('profesor')")
    fun delete(@PathVariable id: Long): ResponseEntity<Unit> {
        questionsService.delete(id)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    @GetMapping("/by-racing/{racingId}")
    @PreAuthorize("hasRole('profesor')")
    fun getByRacingId(@PathVariable racingId: Long): ResponseEntity<List<Questions>> {
        return ResponseEntity(questionsService.getQuestionsByRacingId(racingId), HttpStatus.OK)
    }
}
