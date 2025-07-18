package com.example.Aplicativo_web.service

import com.example.Aplicativo_web.config.JwtUtil
import com.example.Aplicativo_web.entity.Aspirants
import com.example.Aplicativo_web.repository.AspirantsRepository
import com.example.Aplicativo_web.repository.RacingRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import org.slf4j.LoggerFactory // <-- ¡Añade esta importación!

@Service
class AspirantsService {

    // ¡Añade este logger aquí!
    private val logger = LoggerFactory.getLogger(AspirantsService::class.java)

    @Autowired
    lateinit var aspirantsRepository: AspirantsRepository

    @Autowired
    lateinit var jwtUtil: JwtUtil

    @Autowired
    lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    lateinit var racingRepository: RacingRepository

    fun list(): List<Aspirants> {
        logger.info("AspirantsService: Listando todos los aspirantes.")
        return aspirantsRepository.findAll()
    }

    fun save(aspirants: Aspirants): Aspirants {
        logger.info("AspirantsService: Guardando nuevo aspirante: {}", aspirants.username)
        if (aspirants.username.isNullOrBlank()) {
            aspirants.username = generateUsername(aspirants)
            logger.debug("AspirantsService: Username generado: {}", aspirants.username)
        }
        aspirants.password = passwordEncoder.encode(aspirants.password)
        val savedAspirant = aspirantsRepository.save(aspirants)
        logger.info("AspirantsService: Aspirante {} guardado con ID: {}", savedAspirant.username, savedAspirant.id)
        return savedAspirant
    }

    fun update(aspirants: Aspirants): Aspirants {
        val id = aspirants.id ?: run {
            logger.error("AspirantsService: Error al actualizar. ID del aspirante es requerido.")
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "El id del aspirante es requerido")
        }
        logger.info("AspirantsService: Intentando actualizar aspirante con ID: {}", id)
        try {
            aspirantsRepository.findById(id).orElseThrow {
                logger.error("AspirantsService: El aspirante con ID {} no existe para actualizar.", id)
                Exception("El aspirante no existe para actualizar")
            }
            val updatedAspirant = aspirantsRepository.save(aspirants)
            logger.info("AspirantsService: Aspirante con ID {} actualizado exitosamente.", id)
            return updatedAspirant
        } catch (ex: Exception) {
            logger.error("AspirantsService: ERROR al actualizar aspirante con ID {}: {}", id, ex.message, ex)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ex.message)
        }
    }

    fun updateName(aspirants: Aspirants): Aspirants {
        val id = aspirants.id ?: run {
            logger.error("AspirantsService: Error al actualizar nombre. ID del aspirante es requerido.")
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "El id del aspirante es requerido")
        }
        logger.info("AspirantsService: Intentando actualizar nombre de aspirante con ID: {}", id)
        val existingAspirants = aspirantsRepository.findById(id)
            .orElseThrow {
                logger.error("AspirantsService: Aspirante con id {} no encontrado para actualizar nombre.", id)
                ResponseStatusException(HttpStatus.NOT_FOUND, "Aspirante con id $id no encontrado")
            }
        existingAspirants.name = aspirants.name
        val updatedAspirant = aspirantsRepository.save(existingAspirants)
        logger.info("AspirantsService: Nombre de aspirante con ID {} actualizado a '{}'.", id, updatedAspirant.name)
        return updatedAspirant
    }

    fun delete(id: Long?) {
        val idNotNull = id ?: run {
            logger.error("AspirantsService: Error al eliminar. ID es requerido.")
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "El id es requerido")
        }
        logger.info("AspirantsService: Intentando eliminar aspirante con ID: {}", idNotNull)
        val aspirants = aspirantsRepository.findById(idNotNull)
            .orElseThrow {
                logger.error("AspirantsService: Aspirante con ID {} no existe para eliminar.", idNotNull)
                ResponseStatusException(HttpStatus.NOT_FOUND, "Estudiante no existe con el Id: $idNotNull")
            }
        aspirantsRepository.delete(aspirants)
        logger.info("AspirantsService: Aspirante con ID {} eliminado exitosamente.", idNotNull)
    }

    fun findByRacingId(racingId: Long): List<Aspirants> {
        logger.info("AspirantsService: Buscando aspirantes por carrera ID: {}", racingId)
        return aspirantsRepository.findByRacingId(racingId)
    }

    fun findById(id: Long?): Aspirants? {
        val idNotNull = id ?: run {
            logger.warn("AspirantsService: ID de aspirante nulo en findById.")
            return null
        }
        logger.info("AspirantsService: Buscando aspirante por ID: {}", idNotNull)
        return aspirantsRepository.findById(idNotNull).orElse(null)
    }

    fun findByNui(nui: String): Aspirants? {
        logger.info("AspirantsService: Buscando aspirante por NUI: {}", nui)
        return aspirantsRepository.findByNui(nui)
    }

    fun findByUsername(username: String?): Aspirants? {
        if (username.isNullOrBlank()) {
            logger.warn("AspirantsService: Username nulo o en blanco en findByUsername.")
            return null
        }
        logger.info("AspirantsService: Buscando aspirante por username: {}", username)
        return aspirantsRepository.findByUsername(username)
    }

    private fun generateUsername(aspirants: Aspirants): String {
        val base = "${aspirants.name?.lowercase() ?: "asp"}${aspirants.lastName?.lowercase()?.take(4) ?: "user"}"
        val random = (100..999).random()
        val generatedUsername = "$base$random"
        logger.debug("AspirantsService: Generando username: {}", generatedUsername)
        return generatedUsername
    }

    fun login(username: String, password: String): String {
        logger.info("AspirantsService: Intentando login para username: {}", username)
        val aspirant = aspirantsRepository.findByUsername(username)
            ?: run {
                logger.warn("AspirantsService: Login fallido. Usuario '{}' no encontrado.", username)
                throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales incorrectas")
            }

        logger.debug("AspirantsService: Password login: [PROTECTED]")
        logger.debug("AspirantsService: Password en BD: [PROTECTED]")
        val matches = passwordEncoder.matches(password, aspirant.password)
        logger.info("AspirantsService: Coincidencia de contraseña para {}: {}", username, matches)

        if (!matches) {
            logger.warn("AspirantsService: Login fallido. Contraseña incorrecta para usuario '{}'.", username)
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales incorrectas")
        }

        val token = jwtUtil.createForAspirant(
            username = aspirant.username!!,
            aspirantId = aspirant.id!!,
            roles = listOf("ROLE_ASPIRANT")
        )
        logger.info("AspirantsService: Token JWT generado para usuario: {}", username)
        return token
    }

    fun chooseCareer(aspirantId: Long, racingId: Long): Aspirants {
        logger.info("AspirantsService: Intentando asignar carrera {} al aspirante {}.", racingId, aspirantId)
        try {
            val aspirant = aspirantsRepository.findById(aspirantId)
                .orElseThrow {
                    logger.error("AspirantsService: Aspirante no encontrado con ID: {}", aspirantId)
                    ResponseStatusException(HttpStatus.NOT_FOUND, "Aspirante no encontrado con ID: $aspirantId")
                }
            logger.debug("AspirantsService: Aspirante {} encontrado. ¿Ya tiene carrera? {}", aspirantId, aspirant.racing != null)

            if (aspirant.racing != null) {
                logger.warn("AspirantsService: La carrera ya fue asignada al aspirante {} y no puede cambiarse. Carrera actual: {}.", aspirantId, aspirant.racing?.career)
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "La carrera ya fue asignada y no puede cambiarse")
            }

            val racing = racingRepository.findById(racingId)
                .orElseThrow {
                    logger.error("AspirantsService: Carrera no encontrada con ID: {}", racingId)
                    ResponseStatusException(HttpStatus.NOT_FOUND, "Carrera no encontrada con ID: $racingId")
                }
            logger.debug("AspirantsService: Carrera {} encontrada: {}.", racingId, racing.career)

            aspirant.racing = racing
            val updatedAspirant = aspirantsRepository.save(aspirant)
            logger.info("AspirantsService: Aspirante {} actualizado con carrera {}.", aspirantId, racingId)
            return updatedAspirant
        } catch (e: Exception) {
            // Este log capturará CUALQUIER excepción y mostrará el stack trace.
            logger.error("AspirantsService: ERROR CRÍTICO al asignar carrera al aspirante {} con carrera {}: {}", aspirantId, racingId, e.message, e)
            throw e // Re-lanza la excepción para que sea manejada por el controlador o Spring globalmente
        }
    }
}