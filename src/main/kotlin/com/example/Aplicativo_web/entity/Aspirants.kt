package com.example.Aplicativo_web.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "aspirants")
class Aspirants{
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(updatable = false)
    var id: Long? = null
    var name: String? = null

    @Column(name = "last_name", nullable = false, length = 150) // ¡Campo para el apellido añadido!
    var lastName: String? = null// Propiedad en Kotlin

    @Column(nullable = false, unique = true, length = 50)
    var nui: String? = null

    @Column(nullable = false, length = 150)
    var email: String? = null

    @Column(nullable = false, length = 255)
    var password: String? = null // ¡Almacenar HASHED!
}