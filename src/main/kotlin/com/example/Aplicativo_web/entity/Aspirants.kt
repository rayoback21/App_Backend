package com.example.Aplicativo_web.entity

import jakarta.persistence.*

@Entity
@Table(name = "aspirants")
class Aspirants {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    var id: Long? = null

    @Column(nullable = false, length = 50, unique = true)
    var username: String? = null

    @Column(nullable = false, length = 150)
    var name: String? = null

    @Column(name = "last_name", nullable = false, length = 150)
    var lastName: String? = null

    @Column(nullable = false, unique = true, length = 50)
    var nui: String? = null

    @Column(nullable = false, length = 150)
    var email: String? = null

    @Column(nullable = false, length = 255)
    var password: String? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "racing_id", nullable = true)
    var racing: Racing? = null
}
