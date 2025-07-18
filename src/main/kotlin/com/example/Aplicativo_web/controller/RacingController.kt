package com.example.Aplicativo_web.controller

import com.example.Aplicativo_web.dto.RacingRequestDTO
import com.example.Aplicativo_web.entity.Racing
import com.example.Aplicativo_web.service.RacingService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.slf4j.LoggerFactory // <-- Importa esto para el logger

@RestController
@RequestMapping("/racing")
class RacingController(private val racingService: RacingService) {

    // Inicializa el logger para esta clase
    private val logger = LoggerFactory.getLogger(RacingController::class.java)

    @GetMapping
    fun getAll(): List<Racing> {
        logger.info("RacingController: Solicitud GET /racing recibida.")
        try {
            val races = racingService.findAll()
            logger.info("RacingController: Se encontraron {} carreras.", races.size)
            return races
        } catch (e: Exception) {
            // Captura cualquier excepción que ocurra y la loguea con el stack trace completo
            logger.error("RacingController: ERROR INESPERADO al obtener todas las carreras: {}", e.message, e)
            throw e // Vuelve a lanzar la excepción para que Spring la maneje y la redirija a /error
        }
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<Racing> {
        logger.info("RacingController: Solicitud GET /racing/{} recibida.", id)
        try {
            return racingService.findById(id)?.let {
                logger.info("RacingController: Carrera con ID {} encontrada.", id)
                ResponseEntity.ok(it)
            } ?: run {
                logger.warn("RacingController: Carrera con ID {} no encontrada.", id)
                ResponseEntity.notFound().build()
            }
        } catch (e: Exception) {
            logger.error("RacingController: ERROR INESPERADO al obtener carrera por ID {}: {}", id, e.message, e)
            throw e
        }
    }

    @PostMapping
    fun create(@RequestBody request: RacingRequestDTO): ResponseEntity<Racing> {
        logger.info("RacingController: Solicitud POST /racing recibida para carrera: {}", request.career)
        try {
            val created = racingService.createRacing(request)
            logger.info("RacingController: Carrera '{}' creada con ID {}.", created.career, created.id)
            return ResponseEntity.ok(created)
        } catch (e: Exception) {
            logger.error("RacingController: ERROR INESPERADO al crear carrera: {}", e.message, e)
            throw e
        }
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody racing: Racing): ResponseEntity<Racing> {
        logger.info("RacingController: Solicitud PUT /racing/{} recibida para carrera: {}", id, racing.career)
        try {
            val existing = racingService.findById(id) ?: run {
                logger.warn("RacingController: No se puede actualizar. Carrera con ID {} no encontrada.", id)
                return ResponseEntity.notFound().build()
            }
            existing.career = racing.career
            existing.aspirants = racing.aspirants
            existing.professor = racing.professor
            val updated = racingService.save(existing)
            logger.info("RacingController: Carrera con ID {} actualizada exitosamente.", id)
            return ResponseEntity.ok(updated)
        } catch (e: Exception) {
            logger.error("RacingController: ERROR INESPERADO al actualizar carrera con ID {}: {}", id, e.message, e)
            throw e
        }
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        logger.info("RacingController: Solicitud DELETE /racing/{} recibida.", id)
        try {
            val existing = racingService.findById(id) ?: run {
                logger.warn("RacingController: No se puede eliminar. Carrera con ID {} no encontrada.", id)
                return ResponseEntity.notFound().build()
            }
            racingService.deleteById(existing.id!!)
            logger.info("RacingController: Carrera con ID {} eliminada exitosamente.", id)
            return ResponseEntity.noContent().build()
        } catch (e: Exception) {
            logger.error("RacingController: ERROR INESPERADO al eliminar carrera con ID {}: {}", id, e.message, e)
            throw e
        }
    }
}