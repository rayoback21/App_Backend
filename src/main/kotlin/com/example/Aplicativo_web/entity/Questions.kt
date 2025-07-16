package com.example.Aplicativo_web.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@Entity
@Table(name = "questions")
class Questions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    var id: Long? = null

    @Column(name = "text", nullable = true, length = 500)
    var text: String? = null

    @Column(name = "software_q", length = 450)
    var softwareQ: String? = null

    @Column(name = "design_q", length = 450)
    var designQ: String? = null

    @Column(name = "gastronomy_q", length = 450)
    var gastronomyQ: String? = null

    @Column(name = "marketing_q", length = 450)
    var marketingQ: String? = null

    @Column(name = "tourism_q", length = 450)
    var tourismQ: String? = null

    @Column(name = "talent_q", length = 450)
    var talentQ: String? = null

    @Column(name = "nursing_q", length = 450)
    var nursingQ: String? = null

    @Column(name = "electricity_q", length = 450)
    var electricityQ: String? = null

    @Column(name = "accounting_q", length = 450)
    var accountingQ: String? = null

    @Column(name = "networks_q", length = 450)
    var networksQ: String? = null

    @Column(name = "option_a", nullable = false, length = 450) // Frontend siempre envía Option A
    var optionA: String? = null

    @Column(name = "option_b", nullable = false, length = 450) // Frontend siempre envía Option B
    var optionB: String? = null

    // LÓGICA DE NULABILIDAD PARA PREGUNTAS GENERALES:
    // Estas opciones pueden ser nulas si el tipo de pregunta es "Verdadero/Falso"
    // en el frontend.
    @Column(name = "option_c", nullable = true, length = 450)
    var optionC: String? = null

    // LÓGICA DE NULABILIDAD PARA PREGUNTAS GENERALES:
    // Estas opciones pueden ser nulas si el tipo de pregunta es "Verdadero/Falso"
    // en el frontend.
    @Column(name = "option_d", nullable = true, length = 450)
    var optionD: String? = null

    // ¡CAMBIO CRÍTICO AQUÍ!: Cambiar Char? a String?
    // Esto asegura que el tipo de dato en Kotlin coincida con el String enviado desde el frontend.
    // La longitud de la columna en la DB (length = 1) sigue siendo válida para almacenar un solo carácter como String.
    @Column(name = "correct_option", nullable = false, length = 1)
    var correctOption: String? = null // CAMBIO: De Char? a String?

    @Column(name = "question_type", length = 50)
    var questionType: String? = null

    @Column(name = "points", nullable = false) // Frontend siempre envía puntos
    var points: Int = 0

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aspirants_id", nullable = true)
    var aspirants: Aspirants? = null

    // LÓGICA DE NULABILIDAD PARA PREGUNTAS GENERALES:
    // 'professor_id' y 'racing_id' pueden ser nulos para preguntas generales.
    // Para preguntas específicas, el 'QuestionsController' asigna explícitamente
    // estos valores antes de guardar, asegurando que no sean nulos en ese caso.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professor_id", nullable = true) // Ahora permite NULL
    @JsonIgnoreProperties("questions", "roles", "racings", "password")
    var professor: UsersEntity? = null

    // LÓGICA DE NULABILIDAD PARA PREGUNTAS GENERALES:
    // 'professor_id' y 'racing_id' pueden ser nulos para preguntas generales.
    // Para preguntas específicas, el 'QuestionsController' asigna explícitamente
    // estos valores antes de guardar, asegurando que no sean nulos en ese caso.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "racing_id", nullable = true) // Ahora permite NULL
    @JsonIgnoreProperties("hibernateLazyInitializer", "handler")
    var racing: Racing? = null
}