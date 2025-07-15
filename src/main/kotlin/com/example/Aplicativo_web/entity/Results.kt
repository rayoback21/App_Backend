package com.example.Aplicativo_web.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.time.LocalDateTime // Importar LocalDateTime

@Entity
@Table(name = "results")
class Results {
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Id
   @Column(updatable = false)
   var id: Long? = null

   @OneToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "aspirant_id", nullable = false, unique = true)
   var aspirant: Aspirants? = null

   @Column(nullable = true)
   var score: Int? = null

   @Column(name = "exam_date", nullable = true)
   var examDate: LocalDateTime? = null // Cambiado a LocalDateTime

   @Column(name = "correct_answers_count")
   var correctAnswersCount: Int? = null

   @Column(name = "incorrect_answers_count")
   var incorrectAnswersCount: Int? = null

   @Column(name = "unanswered_questions_count") // Añadido
   var unansweredQuestionsCount: Int? = null
}
