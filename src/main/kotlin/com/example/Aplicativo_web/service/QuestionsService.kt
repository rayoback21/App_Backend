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

    @Autowired
    lateinit var questionsRepository: QuestionsRepository

    @Autowired
    lateinit var aspirantsRepository: AspirantsRepository

    @Autowired
    lateinit var usersRepository: UsersRepository

    @Autowired
    lateinit var racingRepository: RacingRepository

    fun list(): List<Questions> = questionsRepository.findAll()

    @Transactional
    fun saveGeneral(questions: Questions): Questions {
        if (questions.text.isNullOrBlank()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "El texto general no puede estar vacío.")
        }

        questions.apply {
            softwareQ = null
            designQ = null
            gastronomyQ = null
            marketingQ = null
            tourismQ = null
            talentQ = null
            nursingQ = null
            electricityQ = null
            accountingQ = null
            networksQ = null
            optionA = null
            optionB = null
            optionC = null
            optionD = null
            correctOption = null
            questionType = null
            professor = null
            racing = null
            aspirants = null
        }
        return questionsRepository.save(questions)
    }

    @Transactional
    fun saveSpecific(questions: Questions): Questions {
        if (questions.professor?.id == null)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Falta id del profesor")
        if (questions.racing?.id == null)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Falta id de la carrera")

        // Validación que la carrera pertenece al profesor autenticado
        val professorId = questions.professor?.id
        val racing = racingRepository.findById(questions.racing!!.id!!)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Carrera no encontrada") }

        if (racing.professor?.id != professorId) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Esta carrera no pertenece al profesor autenticado.")
        }

        questions.racing = racing
        questions.text = null

        val questionTextForRace = questions.softwareQ ?: questions.designQ ?: questions.gastronomyQ ?: questions.marketingQ
        ?: questions.tourismQ ?: questions.talentQ ?: questions.nursingQ ?: questions.electricityQ
        ?: questions.accountingQ ?: questions.networksQ

        if (questionTextForRace.isNullOrBlank())
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe ingresar el texto de la pregunta para la carrera.")

        val nonNullQuestionFields = listOfNotNull(
            questions.softwareQ, questions.designQ, questions.gastronomyQ, questions.marketingQ,
            questions.tourismQ, questions.talentQ, questions.nursingQ, questions.electricityQ,
            questions.accountingQ, questions.networksQ
        ).filter { it.isNotBlank() }

        if (nonNullQuestionFields.size != 1)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Solo debe especificarse el texto para UNA carrera.")

        if (questions.questionType == "true-false") {
            if (questions.optionA.isNullOrBlank() || questions.optionB.isNullOrBlank())
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Opciones A y B obligatorias para verdadero/falso.")
        } else {
            if (listOf(questions.optionA, questions.optionB, questions.optionC, questions.optionD).any { it.isNullOrBlank() })
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Todas las opciones deben tener texto.")
        }

        if (questions.correctOption == null || !listOf('A', 'B', 'C', 'D').contains(questions.correctOption)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe seleccionar una opción correcta válida (A, B, C o D).")
        }

        if (questions.points <= 0)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Puntos deben ser mayores a 0.")

        questions.aspirants = null

        return questionsRepository.save(questions)
    }

    @Transactional
    fun update(questions: Questions): Questions {
        val existingQuestion = questionsRepository.findById(questions.id)
            .orElseThrow {
                ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Pregunta no encontrada con ID: ${questions.id}")
            }

        existingQuestion.apply {
            text = null; softwareQ = null; designQ = null; gastronomyQ = null; marketingQ = null
            tourismQ = null; talentQ = null; nursingQ = null; electricityQ = null
            accountingQ = null; networksQ = null; optionA = null; optionB = null
            optionC = null; optionD = null; correctOption = null; questionType = null

            text = questions.text.takeIf { !it.isNullOrBlank() }
            softwareQ     = questions.softwareQ.takeIf     { !it.isNullOrBlank() }
            designQ       = questions.designQ.takeIf       { !it.isNullOrBlank() }
            gastronomyQ   = questions.gastronomyQ.takeIf   { !it.isNullOrBlank() }
            marketingQ    = questions.marketingQ.takeIf    { !it.isNullOrBlank() }
            tourismQ      = questions.tourismQ.takeIf      { !it.isNullOrBlank() }
            talentQ       = questions.talentQ.takeIf       { !it.isNullOrBlank() }
            nursingQ      = questions.nursingQ.takeIf      { !it.isNullOrBlank() }
            electricityQ  = questions.electricityQ.takeIf  { !it.isNullOrBlank() }
            accountingQ   = questions.accountingQ.takeIf   { !it.isNullOrBlank() }
            networksQ     = questions.networksQ.takeIf     { !it.isNullOrBlank() }

            optionA = questions.optionA.takeIf { !it.isNullOrBlank() }
            optionB = questions.optionB.takeIf { !it.isNullOrBlank() }
            optionC = questions.optionC.takeIf { !it.isNullOrBlank() }
            optionD = questions.optionD.takeIf { !it.isNullOrBlank() }
            correctOption = questions.correctOption.takeIf { it != null && !it.toString().isBlank() }

            questionType  = questions.questionType.takeIf  { !it.isNullOrBlank() }

            professor = questions.professor?.id?.let { pid ->
                usersRepository.findById(pid)
                    .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Profesor no encontrado con ID: $pid") }
            } ?: professor

            racing = questions.racing?.id?.let { rid ->
                racingRepository.findById(rid)
                    .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Carrera no encontrada con ID: $rid") }
            } ?: racing

            aspirants = questions.aspirants?.id?.let { aid ->
                aspirantsRepository.findById(aid)
                    .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Aspirante no encontrado con ID: $aid") }
            }
        }

        if (existingQuestion.points <= 0)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "El valor de puntos debe ser mayor a 0.")

        return questionsRepository.save(existingQuestion)
    }

    @Transactional
    fun updateText(id: Long, newText: String): Questions {
        val question = questionsRepository.findById(id)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Pregunta no encontrada con ID: $id") }

        if (newText.isBlank())
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "El texto no puede estar vacío.")

        question.apply {
            text = newText
            softwareQ = null; designQ = null; gastronomyQ = null; marketingQ = null
            tourismQ = null; talentQ = null; nursingQ = null; electricityQ = null
            accountingQ = null; networksQ = null; optionA = null; optionB = null
            optionC = null; optionD = null; correctOption = null; questionType = null
            professor = null; racing = null
        }

        return questionsRepository.save(question)
    }

    @Transactional
    fun delete(id: Long) {
        val question = questionsRepository.findById(id)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Pregunta no existe con el Id: $id") }
        questionsRepository.delete(question)
    }

    fun getQuestionById(id: Long): Questions =
        questionsRepository.findById(id)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Pregunta no encontrada con ID: $id") }

    fun getQuestionsByAspirantId(aspirantId: Long): List<Questions> {
        aspirantsRepository.findById(aspirantId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Aspirante no encontrado con ID: $aspirantId") }
        return questionsRepository.findByAspirantsId(aspirantId)
    }

    fun getQuestionsByRacingId(racingId: Long): List<Questions> =
        questionsRepository.findByRacingId(racingId)

    fun getQuestionsByProfessorId(professorId: Long): List<Questions> {
        usersRepository.findById(professorId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Profesor no encontrado con ID: $professorId") }
        return questionsRepository.findByProfessorId(professorId)
    }

    fun findProfessorByUsername(username: String): UsersEntity? =
        usersRepository.findByUsername(username)
            .orElse(null)
            ?.takeIf { user -> user.roles.any { it.roles == "profesor" } }
}
