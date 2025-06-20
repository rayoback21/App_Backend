package com.example.Aplicativo_web.service

import com.example.Aplicativo_web.entity.Questions
import com.example.Aplicativo_web.repository.QuestionsRepository
import com.example.Aplicativo_web.repository.AspirantsRepository
import com.example.Aplicativo_web.repository.AdminsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.Optional


@Service
class QuestionsService {
    @Autowired
    lateinit var questionsRepository: QuestionsRepository

    @Autowired
    lateinit var aspirantsRepository: AspirantsRepository

    @Autowired
    lateinit var adminsRepository: AdminsRepository

    fun list(): List<Questions> {
        return questionsRepository.findAll()
    }

    // Método para guardar una pregunta (recibe la entidad completa)
    fun save(questions: Questions): Questions {
        try {
            // Validar y asociar Aspirant si el ID está presente en el objeto questions.aspirants
            questions.aspirants?.id?.let { aspirantId ->
                questions.aspirants = aspirantsRepository.findById(aspirantId)
                    .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Aspirante no encontrado con ID: $aspirantId") }
            } ?: run {
                questions.aspirants = null // Asegura que si viene null en el JSON, se desasocie
            }

            // Validar y asociar Admin si el ID está presente en el objeto questions.admins
            questions.admins?.id?.let { adminId ->
                questions.admins = adminsRepository.findById(adminId)
                    .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Admin no encontrado con ID: $adminId") }
            } ?: run {
                questions.admins = null // Asegura que si viene null en el JSON, se desasocie
            }

            // Opcional: Validación de negocio si una pregunta DEBE tener un aspirante O un admin
            // if (questions.aspirants == null && questions.admins == null) {
            //     throw ResponseStatusException(HttpStatus.BAD_REQUEST, "La pregunta debe estar asociada a un aspirante o un administrador.")
            // }

            // Validar que el texto no sea nulo, según tu entidad
            if (questions.text.isNullOrBlank()) {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "El texto de la pregunta no puede estar vacío.")
            }


            return questionsRepository.save(questions)
        } catch (ex: Exception) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, ex.message ?: "Error al guardar la pregunta")
        }
    }

    // Método para actualizar una pregunta existente (recibe la entidad completa)
    // El cliente debe enviar el ID en la URL y también el ID de la pregunta en el cuerpo (questions.id)
    fun update(questions: Questions): Questions {
        try {
            val existingQuestion = questionsRepository.findById(questions.id)
                .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Pregunta no encontrada con ID: ${questions.id}") }

            // Actualizar solo los campos permitidos. Por ejemplo, el texto.
            existingQuestion.text = questions.text ?: existingQuestion.text // Si el texto es nulo en la entrada, mantiene el existente.

            // Actualizar la asociación con Aspirant
            if (questions.aspirants?.id != null) {
                existingQuestion.aspirants = aspirantsRepository.findById(questions.aspirants!!.id!!)
                    .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Aspirante no encontrado con ID: ${questions.aspirants!!.id}") }
            } else {
                existingQuestion.aspirants = null // Desasocia si el ID es nulo en el JSON
            }

            // Actualizar la asociación con Admin
            if (questions.admins?.id != null) {
                existingQuestion.admins = adminsRepository.findById(questions.admins!!.id!!)
                    .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Admin no encontrado con ID: ${questions.admins!!.id}") }
            } else {
                existingQuestion.admins = null // Desasocia si el ID es nulo en el JSON
            }

            // Validar texto no nulo, etc.
            if (existingQuestion.text.isNullOrBlank()) {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "El texto de la pregunta no puede estar vacío después de la actualización.")
            }

            return questionsRepository.save(existingQuestion)
        } catch (ex: Exception) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, ex.message ?: "Error al actualizar la pregunta")
        }
    }

    // Método para actualizar solo el texto (si PATCH es necesario)
    // Asume que solo se envía { "text": "nuevo texto" }
    fun updateText(id: Long, newText: String): Questions {
        try {
            val existingQuestion = questionsRepository.findById(id)
                .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Pregunta no encontrada con ID: $id") }

            if (newText.isBlank()) {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "El texto no puede estar vacío.")
            }
            existingQuestion.text = newText
            return questionsRepository.save(existingQuestion)
        } catch (ex: Exception) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ex.message ?: "Error al actualizar el texto de la pregunta")
        }
    }

    fun delete(id: Long) {
        try {
            val question = questionsRepository.findById(id)
                .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Pregunta no existe con el Id:  $id") }
            questionsRepository.delete(question)
        } catch (ex: Exception) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al eliminar la Pregunta", ex)
        }
    }

    fun getQuestionById(id: Long): Questions {
        return questionsRepository.findById(id)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Pregunta no encontrada con ID: $id") }
    }

    fun getQuestionsByAspirantId(aspirantId: Long): List<Questions> {
        // Opcional: Validar que el aspirante exista antes de buscar sus preguntas
        aspirantsRepository.findById(aspirantId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Aspirante no encontrado con ID: $aspirantId") }
        return questionsRepository.findByAspirantsId(aspirantId)
    }

    fun getQuestionsByAdminId(adminId: Long): List<Questions> {
        // Opcional: Validar que el admin exista antes de buscar sus preguntas
        adminsRepository.findById(adminId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Admin no encontrado con ID: $adminId") }
        return questionsRepository.findByAdminsId(adminId)
    }
}