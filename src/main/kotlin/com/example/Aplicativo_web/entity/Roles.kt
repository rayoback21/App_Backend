package com.example.Aplicativo_web.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*

@Entity
@Table(name = "roles")
class Roles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    var id: Long? = null

    var roles: String? = null

    @Column(name = "user_id")
    var userId: Long? = null

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    @JsonIgnore  // <--- evita referencia infinita en JSON
    var userEntity: UsersEntity? = null
}
