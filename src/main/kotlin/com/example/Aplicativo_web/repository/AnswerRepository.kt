package com.example.Aplicativo_web.repository

import com.example.Aplicativo_web.entity.Answer
import com.example.Aplicativo_web.entity.Aspirants
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AnswerRepository : JpaRepository<Answer, Long> {
    fun findByAspirantId(aspirantId: Long?): List<Answer> // <--- Changed to Long?
    fun findByQuestionId(questionId: Long?): List<Answer>   // <--- Changed to Long?
    fun findByAspirantIdAndQuestionId(aspirantId: Long?, questionId: Long?): Answer? // <--- Changed to Long? and Long?
}