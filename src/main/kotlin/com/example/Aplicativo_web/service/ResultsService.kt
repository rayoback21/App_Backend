package com.example.Aplicativo_web.service

import com.example.Aplicativo_web.entity.Results
import com.example.Aplicativo_web.repository.RacingRepository
import com.example.Aplicativo_web.repository.ResultsRepository
import org.springframework.stereotype.Service

@Service
class ResultsService(
    private val resultsRepository: ResultsRepository,
    private val racingRepository: RacingRepository
) {

    fun findAll(): List<Results> = resultsRepository.findAll()

    fun findById(id: Long): Results? = resultsRepository.findById(id).orElse(null)

    fun save(results: Results): Results = resultsRepository.save(results)

    fun deleteById(id: Long) = resultsRepository.deleteById(id)

    fun getResultsByProfessorId(professorId: Long): List<Results> {
        val carrerasDelProfesor = racingRepository.findByProfessorId(professorId)
        val carrerasIds: List<Long> = carrerasDelProfesor.mapNotNull { it.id }
        if (carrerasIds.isEmpty()) return emptyList()
        return resultsRepository.findByAspirant_Racing_IdIn(carrerasIds)
    }

}
