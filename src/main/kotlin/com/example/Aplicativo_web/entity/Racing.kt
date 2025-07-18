package com.example.Aplicativo_web.entity

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonManagedReference // <-- Puede que necesites esta si hay ciclo con UsersEntity
import jakarta.persistence.*
import com.example.Aplicativo_web.entity.UsersEntity

@Entity
@Table(name = "racing")
class Racing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    var id: Long? = null

    var career: String? = null

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "professor_id")
    @JsonBackReference // <-- Dejar si UsersEntity tiene @JsonManagedReference para una lista de Racing
    var professor: UsersEntity? = null

    @OneToMany(mappedBy = "racing", fetch = FetchType.LAZY)
    @JsonBackReference // <-- AÑADIR esta anotación aquí para romper el ciclo con Aspirants
    var aspirants: MutableList<Aspirants> = mutableListOf()
}