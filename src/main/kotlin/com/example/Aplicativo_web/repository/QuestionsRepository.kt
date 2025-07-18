package com.example.Aplicativo_web.repository

import com.example.Aplicativo_web.entity.Questions
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface QuestionsRepository : JpaRepository<Questions, Long> {

    fun findById(id: Long?): Optional<Questions>

    fun findByRacingId(racingId: Long): List<Questions>

    fun findByAspirantsId(aspirantsId: Long): List<Questions>

    fun findByProfessorId(professorId: Long): List<Questions>

    // Nuevo método para encontrar preguntas generales
    // Asume que las preguntas generales tienen 'professor' y 'racing' nulos
    // y que el campo 'text' principal de la pregunta no es nulo.
    fun findByProfessorIsNullAndRacingIsNullAndTextIsNotNull(): List<Questions>


    fun findByQuestionType(questionType: String): List<Questions>
    fun findByRacingIdAndQuestionType(racingId: Long, questionType: String): List<Questions>
}