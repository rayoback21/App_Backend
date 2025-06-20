package com.example.Aplicativo_web.service

import com.example.Aplicativo_web.entity.Racing
import com.example.Aplicativo_web.repository.RacingRepository
import org.springframework.stereotype.Service

@Service
class RacingService(private val racingRepository: RacingRepository) {

    fun findAll(): List<Racing> = racingRepository.findAll()

    fun findById(id: Long): Racing? = racingRepository.findById(id).orElse(null)

    fun save(racing: Racing): Racing = racingRepository.save(racing)

    fun deleteById(id: Long) = racingRepository.deleteById(id)
}
