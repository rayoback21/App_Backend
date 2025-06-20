package com.example.Aplicativo_web.repository

import com.example.Aplicativo_web.entity.UsersEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UsersRepository: JpaRepository<UsersEntity, Long> {
    fun findById (id: Long?): UsersEntity
    fun findByUsername(username: String): UsersEntity?
}