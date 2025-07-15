package com.example.Aplicativo_web.entity

import com.fasterxml.jackson.annotation.JsonBackReference
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

    // MODIFICACIÓN: Cambié FetchType.LAZY a FetchType.EAGER para que el profesor se cargue siempre y evitar problemas de null
    @ManyToOne(fetch = FetchType.EAGER)  // <-- Aquí está el cambio
    @JoinColumn(name = "professor_id")
    @JsonBackReference
    var professor: UsersEntity? = null

    @OneToMany(mappedBy = "racing", fetch = FetchType.LAZY)
    var aspirants: MutableList<Aspirants> = mutableListOf()

}
