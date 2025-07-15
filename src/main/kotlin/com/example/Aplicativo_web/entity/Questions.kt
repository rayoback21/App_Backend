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

    @Column(name = "option_a", nullable = false, length = 450)
    var optionA: String? = null

    @Column(name = "option_b", nullable = false, length = 450)
    var optionB: String? = null

    @Column(name = "option_c", nullable = false, length = 450)
    var optionC: String? = null

    @Column(name = "option_d", nullable = false, length = 450)
    var optionD: String? = null

    @Column(name = "correct_option", nullable = false, length = 1)
    var correctOption: Char? = null

    @Column(name = "question_type", length = 50)
    var questionType: String? = null

    @Column(name = "points", nullable = false)
    var points: Int = 0



    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aspirants_id", nullable = true)
    var aspirants: Aspirants? = null

    // 🔴 Corregido para evitar bucle infinito en JSON:
    // Se ignoran las propiedades que causan recursión infinita en 'professor'
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professor_id", nullable = false)
    @JsonIgnoreProperties("questions", "roles", "racings", "password")
    var professor: UsersEntity? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "racing_id", nullable = false)
    @JsonIgnoreProperties("hibernateLazyInitializer", "handler")
    var racing: Racing? = null
}

