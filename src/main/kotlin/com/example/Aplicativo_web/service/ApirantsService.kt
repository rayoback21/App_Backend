package com.example.Aplicativo_web.service

import com.example.Aplicativo_web.entity.Aspirants
import com.example.Aplicativo_web.repository.AspirantsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class ApirantsService {

    @Autowired
    lateinit var aspirantsRepository:  AspirantsRepository

    fun list(): List<Aspirants> {
        return aspirantsRepository.findAll()
    }

    fun save(aspirants: Aspirants): Aspirants {
        return aspirantsRepository.save(aspirants)
    }

    fun update(aspirants: Aspirants): Aspirants {
        try {
            aspirantsRepository.findById(aspirants.id)
                ?: throw Exception("Ya existe el id")
            return aspirantsRepository.save(aspirants)
        } catch (ex: Exception) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ex.message)
        }
    }

    fun updateName(aspirants: Aspirants): Aspirants {
        val existingAspirants = aspirantsRepository.findById(aspirants.id)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Aspirante con id ${aspirants.id} no encontrado") }
        existingAspirants.name = aspirants.name
        return aspirantsRepository.save(existingAspirants)
    }

    fun delete(id: Long) {
        val aspirants = aspirantsRepository.findById(id)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Estudiante no Existe con el Id: $id") }
        aspirantsRepository.delete(aspirants)
    }

    fun findById(id: Long): Aspirants? {
        return aspirantsRepository.findById(id).orElse(null)
    }
    fun findByNui(nui: String): Aspirants? {
        return aspirantsRepository.findByNui(nui)
    }

}
