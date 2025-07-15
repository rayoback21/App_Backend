package com.example.Aplicativo_web.repository

import com.example.Aplicativo_web.entity.UsersEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface UsersRepository: JpaRepository<UsersEntity, Long> {

    override fun findById (id: Long): Optional<UsersEntity>
    fun findByUsername(username: String): Optional<UsersEntity>

}
