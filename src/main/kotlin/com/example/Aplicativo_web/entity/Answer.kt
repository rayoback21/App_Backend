package com.example.Aplicativo_web.entity

import jakarta.persistence.*
import com.fasterxml.jackson.annotation.JsonIgnore

@Entity
@Table(name = "answer")
class Answer {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(updatable = false)
   var id: Long? = null

   @Column(name = "text_a", nullable = false, length = 500)
   var textA: String? = null

   @Column(name = "software_a", nullable = false, length = 450)
   var softwareA: String? = null

   @Column(name = "design_a", nullable = false, length = 450)
   var designA: String? = null

   @Column(name = "gastronomy_a", nullable = false, length = 450)
   var gastronomyA: String? = null

   @Column(name = "marketing_a", nullable = false, length = 450)
   var marketingA: String? = null

   @Column(name = "tourism_a", nullable = false, length = 450)
   var tourismA: String? = null

   @Column(name = "talent_a", nullable = false, length = 450)
   var talentA: String? = null

   @Column(name = "nursing_a", nullable = false, length = 450)
   var nursingA: String? = null

   @Column(name = "electricity_a", nullable = false, length = 450)
   var electricityA: String? = null

   @Column(name = "accounting_a", nullable = false, length = 450)
   var accountingA: String? = null

   @Column(name = "networks_a", nullable = false, length = 450)
   var networksA: String? = null

   @JsonIgnore
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "aspirant_id")
   var aspirant: Aspirants? = null

   @JsonIgnore
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "questions_id")
   var question: Questions? = null

   @Column(name = "selected_option", length = 1)
   var selectedOption: Char? = null

   @Column(name = "is_correct") // You can choose a different column name
   var isCorrect: Boolean = false // Default to false, or make it nullable Boolean? = null
}
