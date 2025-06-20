package com.example.Aplicativo_web.entity

import jakarta.persistence.*

@Entity
@Table(name = "users")
class UsersEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    var id: Long? = null

    var username: String? = null
    var password: String? = null
    var email: String? = null

    @OneToMany(mappedBy = "userEntity", fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    // CAMBIO AQUÍ: Usar MutableList en lugar de List
    var roles: MutableList<Roles> = mutableListOf()
}