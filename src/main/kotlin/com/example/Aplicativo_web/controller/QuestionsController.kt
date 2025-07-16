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

    // LOGICA DE SEGURIDAD: Uso de 'hasAnyAuthority' para roles sin prefijo "ROLE_"
    // Permite a "profesor" y "super_admin" listar preguntas.
    @GetMapping
    @PreAuthorize("hasAnyAuthority('profesor', 'super_admin')")
    fun list(): ResponseEntity<List<Questions>> {
        return ResponseEntity(questionsService.list(), HttpStatus.OK)
    }

    // LOGICA DE SEGURIDAD: Uso de 'hasAnyAuthority' para roles sin prefijo "ROLE_"
    // Permite a "profesor" y "super_admin" ver una pregunta por ID.
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('profesor', 'super_admin')")
    fun getById(@PathVariable id: Long): ResponseEntity<Questions> {
        return ResponseEntity(questionsService.getQuestionById(id), HttpStatus.OK)
    }

    // LOGICA DE SEGURIDAD: Uso de 'hasAuthority' para el rol "profesor".
    @GetMapping("/by-aspirant/{aspirantId}")
    @PreAuthorize("hasAuthority('profesor')")
    fun getByAspirantId(@PathVariable aspirantId: Long): ResponseEntity<List<Questions>> {
        return ResponseEntity(questionsService.getQuestionsByAspirantId(aspirantId), HttpStatus.OK)
    }

    // LOGICA DE SEGURIDAD: Uso de 'hasAuthority' para el rol "profesor".
    @GetMapping("/by-professor/{professorId}")
    @PreAuthorize("hasAuthority('profesor')")
    fun getByProfessorId(@PathVariable professorId: Long): ResponseEntity<List<Questions>> {
        return ResponseEntity(questionsService.getQuestionsByProfessorId(professorId), HttpStatus.OK)
    }

    // LÓGICA PARA CREAR PREGUNTAS GENERALES:
    // - Permite a "super_admin" y "admin" crear preguntas.
    // - Recibe directamente la entidad Questions. La nulabilidad de campos como
    //   optionC, optionD, professor y racing se maneja a nivel de entidad/base de datos.
    @PostMapping("/general")
    @PreAuthorize("hasAnyAuthority('super_admin', 'admin')")
    fun createGeneral(@RequestBody questions: Questions): ResponseEntity<Questions> {
        return ResponseEntity(questionsService.saveGeneral(questions), HttpStatus.CREATED)
    }

    // LÓGICA PARA CREAR PREGUNTAS ESPECÍFICAS (PROFESOR):
    // - Requiere el rol "profesor".
    // - Asigna explícitamente el profesor y la carrera a la pregunta antes de guardar,
    //   asegurando que estos campos no sean nulos para preguntas específicas,
    //   a pesar de que la base de datos ahora los permite como nulos.
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

        println("Recibida pregunta: $questions")
        println("Username autenticado: $username")

        val professor = questionsService.findProfessorByUsername(username)
            ?: return ResponseEntity(HttpStatus.UNAUTHORIZED)

        println("Profesor encontrado: ${professor.id}")

        println("Carreras del profesor:")
        professor.racings.forEach { println(" - ID: ${it.id}, Carrera: ${it.career}") }

        if (questions.racing == null || questions.racing?.id == null) {
            println("Error: La carrera es requerida")
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }

        val requestedRacingId = questions.racing!!.id!!

        val isCareerAuthorized = professor.racings.any { it.id == requestedRacingId }
        if (!isCareerAuthorized) {
            println("Error: Profesor no autorizado para esa carrera (ID: $requestedRacingId)")
            return ResponseEntity(HttpStatus.UNAUTHORIZED)
        }

        questions.professor = professor // LÓGICA CLAVE: Asigna el profesor
        println("Todo ok, guardando pregunta...")
        return ResponseEntity(questionsService.saveSpecific(questions), HttpStatus.CREATED)
    }

    // LÓGICA DE SEGURIDAD: Uso de 'hasAuthority' para el rol "profesor".
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('profesor')")
    fun update(@PathVariable id: Long, @RequestBody questions: Questions): ResponseEntity<Questions> {
        if (questions.id == null || questions.id != id) {
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }
        return ResponseEntity(questionsService.update(questions), HttpStatus.OK)
    }

    // LÓGICA DE SEGURIDAD: Uso de 'hasAuthority' para el rol "profesor".
    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('profesor')")
    fun updateText(@PathVariable id: Long, @RequestBody requestBody: Map<String, String>): ResponseEntity<Questions> {
        val newText = requestBody["text"]
        if (newText.isNullOrBlank()) {
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }
        return ResponseEntity(questionsService.updateText(id, newText), HttpStatus.OK)
    }

    // LÓGICA DE SEGURIDAD: Uso de 'hasAuthority' para el rol "profesor".
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('profesor')")
    fun delete(@PathVariable id: Long): ResponseEntity<Unit> {
        questionsService.delete(id)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    // LÓGICA DE SEGURIDAD: Uso de 'hasAuthority' para el rol "profesor".
    @GetMapping("/by-racing/{racingId}")
    @PreAuthorize("hasAuthority('profesor')")
    fun getByRacingId(@PathVariable racingId: Long): ResponseEntity<List<Questions>> {
        return ResponseEntity(questionsService.getQuestionsByRacingId(racingId), HttpStatus.OK)
    }
}