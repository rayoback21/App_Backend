package com.example.Aplicativo_web.repository

import com.example.Aplicativo_web.entity.UsersEntity
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository

interface UsersRepository : JpaRepository<UsersEntity, Long> {

    fun findByUsername(username: String): UsersEntity?

    // Si quieres que cargue las carreras del profesor automáticamente
    @EntityGraph(attributePaths = ["racings"])
    fun findWithRacingsByUsername(username: String): UsersEntity?
}
