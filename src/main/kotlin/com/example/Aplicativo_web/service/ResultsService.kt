package com.example.Aplicativo_web.service

import com.example.Aplicativo_web.entity.Results
import com.example.Aplicativo_web.repository.ResultsRepository
import org.springframework.stereotype.Service

@Service
class ResultsService(private val resultsRepository: ResultsRepository) {

    fun findAll(): List<Results> = resultsRepository.findAll()

    fun findById(id: Long): Results? = resultsRepository.findById(id).orElse(null)

    fun save(results: Results): Results = resultsRepository.save(results)

    fun deleteById(id: Long) = resultsRepository.deleteById(id)
}
