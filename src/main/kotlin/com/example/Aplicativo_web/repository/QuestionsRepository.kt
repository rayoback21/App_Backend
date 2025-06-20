package com.example.Aplicativo_web.repository

import com.example.Aplicativo_web.entity.Questions
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface QuestionsRepository : JpaRepository<Questions, Long> {

     fun findById(id: Long?): Optional<Questions>

    // Corrected methods to search by the foreign keys
    fun findByAspirantsId(aspirantsId: Long): List<Questions>
    fun findByAdminsId(adminsId: Long): List<Questions>
}