package com.example.Aplicativo_web.service

import com.example.Aplicativo_web.dto.RacingRequestDTO
import com.example.Aplicativo_web.entity.Racing
import com.example.Aplicativo_web.repository.RacingRepository
import com.example.Aplicativo_web.repository.UsersRepository
import org.springframework.stereotype.Service

@Service
class RacingService(
    private val racingRepository: RacingRepository,
    private val usersRepository: UsersRepository
) {

    fun findAll(): List<Racing> = racingRepository.findAll()

    fun findById(id: Long): Racing? = racingRepository.findById(id).orElse(null)

    fun save(racing: Racing): Racing = racingRepository.save(racing)

    fun deleteById(id: Long) = racingRepository.deleteById(id)

    fun findByProfessorId(professorId: Long): List<Racing> = racingRepository.findByProfessorId(professorId)

    fun createRacing(request: RacingRequestDTO): Racing {
        val professor = usersRepository.findById(request.professorId)
            .orElseThrow { RuntimeException("Profesor no encontrado con ID: ${request.professorId}") }

        val racing = Racing().apply {
            career = request.career
            this.professor = professor
        }

        return racingRepository.save(racing)
    }
}
