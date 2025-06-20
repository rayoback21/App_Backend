package com.example.Aplicativo_web.repository

import com.example.Aplicativo_web.entity.Admins
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface AdminsRepository : JpaRepository<Admins, Long> {
    fun findById (id: Long?): Admins?
    }
