package com.example.Aplicativo_web.repository

import com.example.Aplicativo_web.entity.Racing
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RacingRepository : JpaRepository<Racing, Long> {
    fun findById(id: Long?): Racing?
    fun findByProfessorId(professorId: Long): List<Racing> // Modificado: buscar carreras por profesor
} // Modificación aplicada aquí