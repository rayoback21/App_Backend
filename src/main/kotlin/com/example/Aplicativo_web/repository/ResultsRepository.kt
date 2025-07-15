package com.example.Aplicativo_web.repository

import com.example.Aplicativo_web.entity.Results
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ResultsRepository : JpaRepository<Results, Long> {
    fun findById(id: Long?): Results?

    fun findByAspirant_Racing_IdIn(racingIds: List<Long>): List<Results>
}
