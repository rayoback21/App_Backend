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

    fun getGeneralQuestions(): List<Questions> {
        return questionsRepository.findByProfessorIsNullAndRacingIsNullAndTextIsNotNull()
    }

    @Transactional
    fun saveGeneral(questions: Questions): Questions {
        if (questions.text.isNullOrBlank()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "El texto general no puede estar vacío.")
        }

        questions.apply {
            softwareQ = null; designQ = null; gastronomyQ = null; marketingQ = null
            tourismQ = null; talentQ = null; nursingQ = null; electricityQ = null
            accountingQ = null; networksQ = null;
            professor = null;
            racing = null;
            aspirants = null
        }

        if (questions.optionA.isNullOrBlank() || questions.optionB.isNullOrBlank()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Las opciones A y B son obligatorias para preguntas generales.")
        }
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
        } else {
            if (listOf(questions.optionA, questions.optionB, questions.optionC, questions.optionD).any { it.isNullOrBlank() })
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Todas las opciones deben tener texto para preguntas de opción múltiple.")
        }

        if (questions.correctOption == null || !listOf("A", "B", "C", "D").contains(questions.correctOption)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe seleccionar una opción correcta válida (A, B, C o D).")
        }

        if (questions.points <= 0)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Puntos deben ser mayores a 0.")

        questions.aspirants = null

        return questionsRepository.save(questions)
    }

    @Transactional
    fun update(id: Long, questions: Questions): Questions { // <-- AÑADIDO: id como parámetro
        val existingQuestion = questionsRepository.findById(id).orElse(null) // Usar el 'id' del path
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Pregunta no encontrada con ID: $id")

        if (questions.id == null || questions.id != id) { // <-- Ahora 'id' está definido
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "El ID de la pregunta en el cuerpo no coincide con el ID de la URL.")
        }
        existingQuestion.apply {
            softwareQ = null; designQ = null; gastronomyQ = null; marketingQ = null
            tourismQ = null; talentQ = null; nursingQ = null; electricityQ = null
            accountingQ = null; networksQ = null;

            text = questions.text?.takeIf { it.isNotBlank() }

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

            optionA = questions.optionA?.takeIf { it.isNotBlank() }
            optionB = questions.optionB?.takeIf { it.isNotBlank() }
            optionC = questions.optionC?.takeIf { it.isNotBlank() }
            optionD = questions.optionD?.takeIf { it.isNotBlank() }
            correctOption = questions.correctOption?.takeIf { it.isNotBlank() }

            questionType = questions.questionType?.takeIf { it.isNotBlank() }

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