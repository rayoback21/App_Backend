package com.example.Aplicativo_web.service

import com.example.Aplicativo_web.entity.Questions
import com.example.Aplicativo_web.entity.UsersEntity
import com.example.Aplicativo_web.repository.QuestionsRepository
import com.example.Aplicativo_web.repository.AspirantsRepository
import com.example.Aplicativo_web.repository.UsersRepository
import com.example.Aplicativo_web.repository.RacingRepository
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class QuestionsService {

    @Autowired lateinit var questionsRepository: QuestionsRepository
    @Autowired lateinit var aspirantsRepository: AspirantsRepository
    @Autowired lateinit var usersRepository: UsersRepository
    @Autowired lateinit var racingRepository: RacingRepository

    fun list(): List<Questions> = questionsRepository.findAll()

    @Transactional
    fun saveGeneral(questions: Questions): Questions {
        if (questions.text.isNullOrBlank()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "El texto general no puede estar vacío.")
        }

        questions.apply {
            // Aseguramos que estos campos sean null para preguntas generales
            // (La entidad Questions.kt ahora permite que sean nulos en la DB)
            softwareQ = null; designQ = null; gastronomyQ = null; marketingQ = null
            tourismQ = null; talentQ = null; nursingQ = null; electricityQ = null
            accountingQ = null; networksQ = null;
            // Opciones y correctOption son enviados por el frontend, solo asignamos.
            // Para preguntas generales, optionC/D pueden ser nulos si es V/F.
            // correctOption siempre debería venir y se valida abajo.
            // Los valores de optionA, optionB, correctOption, questionType, points
            // se esperan directamente del payload y se validan a continuación.
            professor = null; // Siempre null para preguntas generales
            racing = null;    // Siempre null para preguntas generales
            aspirants = null // Siempre null para preguntas generales
        }

        // Validación de opciones y correctOption para preguntas generales
        // Asumiendo que para preguntas generales siempre vienen correctOption, optionA, optionB
        if (questions.optionA.isNullOrBlank() || questions.optionB.isNullOrBlank()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Las opciones A y B son obligatorias para preguntas generales.")
        }
        // Validar correctOption para preguntas generales
        if (questions.correctOption == null || !listOf("A", "B", "C", "D").contains(questions.correctOption)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe seleccionar una opción correcta válida (A, B, C o D) para preguntas generales.")
        }


        return questionsRepository.save(questions)
    }

    @Transactional
    fun saveSpecific(questions: Questions): Questions {
        val professorId = questions.professor?.id
            ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Falta id del profesor")
        val racingId = questions.racing?.id
            ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Falta id de la carrera")

        val racing = racingRepository.findById(racingId).orElse(null)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Carrera no encontrada")

        if (racing.professor?.id != professorId) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Esta carrera no pertenece al profesor autenticado.")
        }

        questions.racing = racing
        questions.text = null

        val questionTextForRace = listOfNotNull(
            questions.softwareQ, questions.designQ, questions.gastronomyQ, questions.marketingQ,
            questions.tourismQ, questions.talentQ, questions.nursingQ, questions.electricityQ,
            questions.accountingQ, questions.networksQ
        ).filter { it.isNotBlank() }

        if (questionTextForRace.isEmpty())
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe ingresar el texto de la pregunta para la carrera.")

        if (questionTextForRace.size != 1)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Solo debe especificarse el texto para UNA carrera.")

        if (questions.questionType == "true-false") {
            if (questions.optionA.isNullOrBlank() || questions.optionB.isNullOrBlank())
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Opciones A y B obligatorias para verdadero/falso.")
            // Para V/F, optionC y optionD pueden ser nulos, no es necesario validarlos aquí.
        } else { // Asumimos "multiple-choice"
            // Se asume que para "multiple-choice", optionC y optionD siempre tienen texto.
            if (listOf(questions.optionA, questions.optionB, questions.optionC, questions.optionD).any { it.isNullOrBlank() })
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Todas las opciones deben tener texto para preguntas de opción múltiple.")
        }

        // LÓGICA CLAVE: Cambiar la lista de Char a String para que coincida con questions.correctOption (String?)
        if (questions.correctOption == null || !listOf("A", "B", "C", "D").contains(questions.correctOption)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe seleccionar una opción correcta válida (A, B, C o D).")
        }

        if (questions.points <= 0)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Puntos deben ser mayores a 0.")

        questions.aspirants = null

        return questionsRepository.save(questions)
    }

    @Transactional
    fun update(questions: Questions): Questions {
        val existingQuestion = questions.id?.let { questionsRepository.findById(it).orElse(null) }
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Pregunta no encontrada con ID: ${questions.id}")

        existingQuestion.apply {
            // Se limpian los campos de carrera/texto general para evitar conflictos al actualizar.
            // Esto es crucial para que la pregunta mantenga su tipo (general o específica).
            // Si la pregunta que se actualiza es general, estos se mantendrán nulos.
            // Si es específica, los campos de texto específico y racing/professor se mantendrán.
            // Para evitar nullificar campos que no deberían serlo si la pregunta no cambia de tipo
            // la lógica aquí puede ser un poco más sofisticada o manejarlo en el DTO/controlador.
            // Por ahora, se asume que si el cliente envía null, se establece a null.
            softwareQ = null; designQ = null; gastronomyQ = null; marketingQ = null
            tourismQ = null; talentQ = null; nursingQ = null; electricityQ = null
            accountingQ = null; networksQ = null;
            // Las opciones y correctOption no se deberían nullificar aquí indiscriminadamente
            // porque son parte de la pregunta en sí. Solo se actualizan si se envían valores nuevos.


            text = questions.text?.takeIf { it.isNotBlank() } // Actualiza solo si no es nulo/vacío

            // Actualiza los campos específicos de carrera solo si se proporcionan
            softwareQ = questions.softwareQ?.takeIf { it.isNotBlank() }
            designQ = questions.designQ?.takeIf { it.isNotBlank() }
            gastronomyQ = questions.gastronomyQ?.takeIf { it.isNotBlank() }
            marketingQ = questions.marketingQ?.takeIf { it.isNotBlank() }
            tourismQ = questions.tourismQ?.takeIf { it.isNotBlank() }
            talentQ = questions.talentQ?.takeIf { it.isNotBlank() }
            nursingQ = questions.nursingQ?.takeIf { it.isNotBlank() }
            electricityQ = questions.electricityQ?.takeIf { it.isNotBlank() }
            accountingQ = questions.accountingQ?.takeIf { it.isNotBlank() }
            networksQ = questions.networksQ?.takeIf { it.isNotBlank() }

            // Actualiza opciones solo si se proporcionan
            optionA = questions.optionA?.takeIf { it.isNotBlank() }
            optionB = questions.optionB?.takeIf { it.isNotBlank() }
            optionC = questions.optionC?.takeIf { it.isNotBlank() }
            optionD = questions.optionD?.takeIf { it.isNotBlank() }
            correctOption = questions.correctOption?.takeIf { it.isNotBlank() } // Usar isNotBlank() para String

            questionType = questions.questionType?.takeIf { it.isNotBlank() }

            // Lógica para actualizar profesor y carrera si se envían.
            // Se mantiene el valor existente si no se proporciona uno nuevo.
            professor = questions.professor?.id?.let {
                usersRepository.findById(it).orElse(null)
                    ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Profesor no encontrado con ID: $it")
            } ?: professor

            racing = questions.racing?.id?.let {
                racingRepository.findById(it).orElse(null)
                    ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Carrera no encontrada con ID: $it")
            } ?: racing

            aspirants = questions.aspirants?.id?.let {
                aspirantsRepository.findById(it).orElse(null)
                    ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Aspirante no encontrado con ID: $it")
            }
        }

        if (existingQuestion.points <= 0)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "El valor de puntos debe ser mayor a 0.")

        return questionsRepository.save(existingQuestion)
    }

    @Transactional
    fun updateText(id: Long, newText: String): Questions {
        val question = questionsRepository.findById(id).orElse(null)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Pregunta no encontrada con ID: $id")

        if (newText.isBlank())
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "El texto no puede estar vacío.")

        question.apply {
            text = newText
            // Al actualizar solo el texto general, los campos específicos de carrera
            // y las opciones (incluyendo correctOption), profesor y racing se mantienen.
            // No se deben nullificar indiscriminadamente aquí.
            // Si el objetivo es convertir una pregunta específica en general (o viceversa)
            // se necesita una lógica de negocio más explícita o un endpoint diferente.
            // Por ahora, solo se actualiza el texto principal.
            // Si estos campos deben ser null, el cliente debe enviarlos como null en el PATCH.
        }

        return questionsRepository.save(question)
    }

    @Transactional
    fun delete(id: Long) {
        val question = questionsRepository.findById(id).orElse(null)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Pregunta no existe con el Id: $id")
        questionsRepository.delete(question)
    }

    fun getQuestionById(id: Long): Questions =
        questionsRepository.findById(id).orElse(null)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Pregunta no encontrada con ID: $id")

    fun getQuestionsByAspirantId(aspirantId: Long): List<Questions> {
        aspirantsRepository.findById(aspirantId).orElse(null)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Aspirante no encontrado con ID: $aspirantId")
        return questionsRepository.findByAspirantsId(aspirantId)
    }

    fun getQuestionsByRacingId(racingId: Long): List<Questions> =
        questionsRepository.findByRacingId(racingId)

    fun getQuestionsByProfessorId(professorId: Long): List<Questions> {
        usersRepository.findById(professorId).orElse(null)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Profesor no encontrado con ID: $professorId")
        return questionsRepository.findByProfessorId(professorId)
    }

    fun findProfessorByUsername(username: String): UsersEntity? =
        usersRepository.findByUsername(username)
            ?.takeIf { user -> user.roles.any { it.roles == "profesor" } }

    fun findRacingById(id: Long) =
        racingRepository.findById(id).orElse(null)
}