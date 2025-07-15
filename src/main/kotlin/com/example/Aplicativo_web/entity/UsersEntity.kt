package com.example.Aplicativo_web.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.*

@JsonIgnoreProperties(value = ["hibernateLazyInitializer", "handler"])
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
    var roles: MutableList<Roles> = mutableListOf()

    var photoUrl: String? = null

    @OneToMany(mappedBy = "professor")
    @JsonIgnore
    var questions: MutableList<Questions> = mutableListOf()

    @OneToMany(mappedBy = "professor")
    @JsonIgnore
    var racings: MutableList<Racing> = mutableListOf()
}
