package com.example.Aplicativo_web.repository

import com.example.Aplicativo_web.entity.Aspirants
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface AspirantsRepository:JpaRepository<Aspirants, Long> {
    fun findById(id: Long?): Optional<Aspirants>
    fun findByNui(nui: String): Aspirants?

}