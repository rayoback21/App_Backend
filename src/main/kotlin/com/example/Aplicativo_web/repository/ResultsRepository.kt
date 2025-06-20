package com.example.Aplicativo_web.repository

import com.example.Aplicativo_web.entity.Aspirants
import com.example.Aplicativo_web.entity.Results
import jakarta.persistence.Id
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ResultsRepository : JpaRepository<Results, Long> {
    fun findById(id: Long?): Results?
}