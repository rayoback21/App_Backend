package com.example.Aplicativo_web.repository

import com.example.Aplicativo_web.entity.Questions
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface QuestionsRepository : JpaRepository<Questions, Long> {

    fun findById(id: Long?): Optional<Questions>
    fun findByRacingId(racingId: Long): List<Questions> // Preguntas específicas de una carrera

    fun findByAspirantsId(aspirantsId: Long): List<Questions> // Si esta relación tiene sentido para tu lógica
    fun findByProfessorId(professorId: Long): List<Questions> // <--- ¡AJUSTE CLAVE AQUÍ!

    fun findByQuestionType(questionType: String): List<Questions> // Para preguntas generales o de un tipo específico
    fun findByRacingIdAndQuestionType(racingId: Long, questionType: String): List<Questions> // Para preguntas de una carrera y un tipo específico
}
