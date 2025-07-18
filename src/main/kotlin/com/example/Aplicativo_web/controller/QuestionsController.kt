package com.example.Aplicativo_web.controller

import com.example.Aplicativo_web.entity.Questions
import com.example.Aplicativo_web.service.QuestionsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/questions")
@CrossOrigin(methods = [RequestMethod.GET, RequestMethod.POST, RequestMethod.PATCH, RequestMethod.PUT, RequestMethod.DELETE])
class QuestionsController {

    @Autowired
    private lateinit var questionsService: QuestionsService

    // Listado completo de preguntas, sólo profesores y super admins
    @GetMapping
    @PreAuthorize("hasAnyAuthority('profesor', 'super_admin')")
    fun list(): ResponseEntity<List<Questions>> {
        return ResponseEntity(questionsService.list(), HttpStatus.OK)
    }

    // Preguntas generales accesibles a aspirantes, profesores, admins y super admins
    @GetMapping("/general")
    @PreAuthorize("hasAnyAuthority('ROLE_ASPIRANT', 'profesor', 'super_admin', 'admin')")
    fun getGeneralQuestionsForAspirant(): ResponseEntity<List<Questions>> {
        return ResponseEntity(questionsService.getGeneralQuestions(), HttpStatus.OK)
    }

    // Obtener pregunta por id, sólo profesores y super admins
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('profesor', 'super_admin')")
    fun getById(@PathVariable id: Long): ResponseEntity<Questions> {
        return ResponseEntity(questionsService.getQuestionById(id), HttpStatus.OK)
    }

    // Obtener preguntas asignadas a un aspirante, sólo profesores
    @GetMapping("/by-aspirant/{aspirantId}")
    @PreAuthorize("hasAuthority('profesor')")
    fun getByAspirantId(@PathVariable aspirantId: Long): ResponseEntity<List<Questions>> {
        return ResponseEntity(questionsService.getQuestionsByAspirantId(aspirantId), HttpStatus.OK)
    }

    // Obtener preguntas por profesor, sólo profesores
    @GetMapping("/by-professor/{professorId}")
    @PreAuthorize("hasAuthority('profesor')")
    fun getByProfessorId(@PathVariable professorId: Long): ResponseEntity<List<Questions>> {
        return ResponseEntity(questionsService.getQuestionsByProfessorId(professorId), HttpStatus.OK)
    }

    // Crear pregunta general, sólo super admins y admins
    @PostMapping("/general")
    @PreAuthorize("hasAnyAuthority('super_admin', 'admin')")
    fun createGeneral(@RequestBody questions: Questions): ResponseEntity<Questions> {
        return ResponseEntity(questionsService.saveGeneral(questions), HttpStatus.CREATED)
    }

    // Crear pregunta específica, sólo profesores
    @PostMapping("/specific")
    @PreAuthorize("hasAuthority('profesor')")
    fun createSpecific(@RequestBody questions: Questions): ResponseEntity<Questions> {
        val authentication = SecurityContextHolder.getContext().authentication
        val principal = authentication.principal
        val username = when (principal) {
            is UserDetails -> principal.username
            is String -> principal
            else -> return ResponseEntity(HttpStatus.UNAUTHORIZED)
        }

        val professor = questionsService.findProfessorByUsername(username)
            ?: return ResponseEntity(HttpStatus.UNAUTHORIZED)

        if (questions.racing == null || questions.racing?.id == null) {
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }

        val requestedRacingId = questions.racing!!.id!!

        val isCareerAuthorized = professor.racings.any { it.id == requestedRacingId }
        if (!isCareerAuthorized) {
            return ResponseEntity(HttpStatus.UNAUTHORIZED)
        }

        questions.professor = professor
        return ResponseEntity(questionsService.saveSpecific(questions), HttpStatus.CREATED)
    }

    // Actualizar pregunta completa, sólo profesores
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('profesor')")
    fun update(@PathVariable id: Long, @RequestBody questions: Questions): ResponseEntity<Questions> {
        if (questions.id == null || questions.id != id) {
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }
        return ResponseEntity(questionsService.update(id, questions), HttpStatus.OK)
    }

    // Actualizar sólo el texto de la pregunta, sólo profesores
    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('profesor')")
    fun updateText(@PathVariable id: Long, @RequestBody requestBody: Map<String, String>): ResponseEntity<Questions> {
        val newText = requestBody["text"]
        if (newText.isNullOrBlank()) {
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }
        return ResponseEntity(questionsService.updateText(id, newText), HttpStatus.OK)
    }

    // Eliminar pregunta, sólo profesores
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('profesor')")
    fun delete(@PathVariable id: Long): ResponseEntity<Unit> {
        questionsService.delete(id)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    // Obtener preguntas por carrera, accesible a profesores y aspirantes
    @GetMapping("/by-racing/{racingId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ASPIRANT', 'profesor')")
    fun getByRacingId(@PathVariable racingId: Long): ResponseEntity<List<Questions>> {
        return ResponseEntity(questionsService.getQuestionsByRacingId(racingId), HttpStatus.OK)
    }
}
