package com.example.Aplicativo_web.service

import com.example.Aplicativo_web.entity.Answer
import com.example.Aplicativo_web.repository.AnswerRepository
import com.example.Aplicativo_web.repository.AspirantsRepository
import com.example.Aplicativo_web.repository.QuestionsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.Optional

@Service
class AnswerService {

    @Autowired
    lateinit var answerRepository: AnswerRepository

    @Autowired
    lateinit var aspirantsRepository: AspirantsRepository

    @Autowired
    lateinit var questionsRepository: QuestionsRepository

    fun list(): List<Answer> {
        return answerRepository.findAll()
    }

    fun save(answer: Answer): Answer {
        try {
            answer.aspirant?.id?.let { aspirantId ->
                answer.aspirant = aspirantsRepository.findById(aspirantId)
                    .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Aspirante no encontrado con ID: $aspirantId") }
            } ?: run {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Se requiere el ID del aspirante para guardar la respuesta.")
            }

            answer.question?.id?.let { questionId ->
                answer.question = questionsRepository.findById(questionId)
                    .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Pregunta no encontrada con ID: $questionId") }
            } ?: run {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Se requiere el ID de la pregunta para guardar la respuesta.")
            }

            if (answer.selectedOption == null) {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "La opción seleccionada no puede ser nula.")
            }

            return answerRepository.save(answer)
        } catch (ex: Exception) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, ex.message ?: "Error al guardar la respuesta")
        }
    }

    fun update(answer: Answer): Answer {
        try {
            val answerId = answer.id ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "El ID de la respuesta no puede ser nulo para actualizar.")
            val existingAnswer = answerRepository.findById(answerId)
                .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Respuesta no encontrada con ID: ${answer.id}") }

            existingAnswer.selectedOption = answer.selectedOption ?: existingAnswer.selectedOption

            existingAnswer.isCorrect = answer.isCorrect

            return answerRepository.save(existingAnswer)
        } catch (ex: Exception) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, ex.message ?: "Error al actualizar la respuesta")
        }
    }

    fun delete(id: Long) {
        try {
            val answer = answerRepository.findById(id)
                .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Respuesta no existe con el Id:  $id") }
            answerRepository.delete(answer)
        } catch (ex: Exception) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al eliminar la Respuesta", ex)
        }
    }

    fun getAnswerById(id: Long): Answer {
        return answerRepository.findById(id)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Respuesta no encontrada con ID: $id") }
    }

    fun getAnswersByAspirantId(aspirantId: Long): List<Answer> {
        aspirantsRepository.findById(aspirantId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Aspirante no encontrado con ID: $aspirantId") }
        return answerRepository.findByAspirantId(aspirantId)
    }

    fun getAnswersByQuestionId(questionId: Long): List<Answer> {
        questionsRepository.findById(questionId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Pregunta no encontrada con ID: $questionId") }
        return answerRepository.findByQuestionId(questionId)
    }

    fun getAnswerByAspirantAndQuestion(aspirantId: Long, questionId: Long): Answer {
        return answerRepository.findByAspirantIdAndQuestionId(aspirantId, questionId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Respuesta no encontrada para Aspirante ID: $aspirantId y Pregunta ID: $questionId")
    }
}