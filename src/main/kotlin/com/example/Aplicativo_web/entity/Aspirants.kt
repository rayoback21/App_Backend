package com.example.Aplicativo_web.entity

import jakarta.persistence.*
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonManagedReference // <-- AÑADIR esta importación

@Entity
@Table(name = "aspirants")
class Aspirants {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    var id: Long? = null

    var username: String? = null
    var name: String? = null
    var lastName: String? = null
    var nui: String? = null
    var email: String? = null
    var password: String? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "racing_id", nullable = true)
    @JsonIgnoreProperties("hibernateLazyInitializer", "handler")
    @JsonInclude(JsonInclude.Include.ALWAYS)
    @JsonManagedReference // <-- AÑADIR esta anotación aquí
    var racing: Racing? = null
}