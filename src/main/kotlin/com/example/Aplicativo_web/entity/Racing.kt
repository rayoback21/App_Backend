package com.example.Aplicativo_web.entity

import jakarta.persistence.*

@Entity
@Table(name = "racing")
data class Racing(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "career", nullable = false, length = 350)
    var career: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aspirants_id")
    var aspirants: Aspirants? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admins_id")
    var admins: Admins? = null
)
