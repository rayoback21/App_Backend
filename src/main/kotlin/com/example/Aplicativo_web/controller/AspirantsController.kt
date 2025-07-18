package com.example.Aplicativo_web.controller

import com.example.Aplicativo_web.config.JwtUtil
import com.example.Aplicativo_web.dto.AspirantsRegisterDto
import com.example.Aplicativo_web.dto.LoginDto
import com.example.Aplicativo_web.entity.Aspirants
import com.example.Aplicativo_web.service.AspirantsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.slf4j.LoggerFactory // <-- ¡Añade esta importación!

@RestController
@RequestMapping("/aspirants")
@CrossOrigin(
    origins = ["http://localhost:5173"], // Asegúrate que esta URL sea la de tu frontend
    allowedHeaders = ["*"],
    allowCredentials = "true",
    methods = [RequestMethod.GET, RequestMethod.POST, RequestMethod.PATCH, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS]
)
class AspirantsController {

    // ¡Añade este logger aquí!
    private val logger = LoggerFactory.getLogger(AspirantsController::class.java)

    @Autowired
    lateinit var aspirantsService: AspirantsService

    @Autowired
    lateinit var jwtUtil: JwtUtil

    @Autowired
    lateinit var passwordEncoder: PasswordEncoder

    @GetMapping
    fun list(): List<Aspirants> {
        logger.info("AspirantsController: Solicitud GET /aspirants recibida.")
        return aspirantsService.list()
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<Aspirants> {
        logger.info("AspirantsController: Solicitud GET /aspirants/{} recibida.", id)
        val aspirant = aspirantsService.findById(id)
        return if (aspirant != null) {
            logger.info("AspirantsController: Aspirante con ID {} encontrado.", id)
            ResponseEntity.ok(aspirant)
        } else {
            logger.warn("AspirantsController: Aspirante con ID {} no encontrado.", id)
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @GetMapping("/nui/{nui}")
    fun getByNui(@PathVariable nui: String): ResponseEntity<Aspirants> {
        logger.info("AspirantsController: Solicitud GET /aspirants/nui/{} recibida.", nui)
        val aspirant = aspirantsService.findByNui(nui)
        return if (aspirant != null) {
            logger.info("AspirantsController: Aspirante con NUI {} encontrado.", nui)
            ResponseEntity.ok(aspirant)
        } else {
            logger.warn("AspirantsController: Aspirante con NUI {} no encontrado.", nui)
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @GetMapping("/racing/{racingId}")
    fun getByRacing(@PathVariable racingId: Long): ResponseEntity<List<Aspirants>> {
        logger.info("AspirantsController: Solicitud GET /aspirants/racing/{} recibida.", racingId)
        val aspirants = aspirantsService.findByRacingId(racingId)
        logger.info(
            "AspirantsController: Se encontraron {} aspirantes para la carrera ID {}.",
            aspirants.size,
            racingId
        )
        return ResponseEntity.ok(aspirants)
    }

    @PostMapping("/register")
    fun register(@RequestBody dto: AspirantsRegisterDto): ResponseEntity<Aspirants> {
        logger.info("AspirantsController: Solicitud POST /aspirants/register recibida para usuario: {}", dto.username)
        val aspirant = Aspirants().apply {
            name = dto.name
            lastName = dto.lastName
            username = dto.username
            nui = dto.nui
            email = dto.email
            password = dto.password
            racing = null
        }
        val saved = aspirantsService.save(aspirant)
        logger.info("AspirantsController: Aspirante {} registrado con éxito.", saved.username)
        return ResponseEntity.ok(saved)
    }

    @PostMapping("/login")
    fun login(@RequestBody loginDto: LoginDto): ResponseEntity<Any> {
        logger.info("AspirantsController: Solicitud POST /aspirants/login recibida para usuario: {}", loginDto.username)
        return try {
            val token = aspirantsService.login(loginDto.username ?: "", loginDto.password ?: "")
            val aspirant = aspirantsService.findByUsername(loginDto.username)
            logger.info("AspirantsController: Login exitoso para usuario: {}", loginDto.username)
            ResponseEntity.ok(mapOf("token" to token, "aspirant" to aspirant))
        } catch (ex: ResponseStatusException) {
            logger.warn(
                "AspirantsController: Error de login (ResponseStatusException) para usuario {}: {}",
                loginDto.username,
                ex.reason
            )
            val status = HttpStatus.resolve(ex.statusCode.value()) ?: HttpStatus.INTERNAL_SERVER_ERROR
            ResponseEntity(mapOf("error" to (ex.reason ?: "Credenciales incorrectas")), status)
        } catch (ex: Exception) {
            logger.error(
                "AspirantsController: ERROR INESPERADO durante el login para usuario {}: {}",
                loginDto.username,
                ex.message,
                ex
            )
            ResponseEntity(
                mapOf("error" to "Error interno del servidor durante el login."),
                HttpStatus.INTERNAL_SERVER_ERROR
            )
        }
    }

    @GetMapping("/me")
    fun getMyAspirantDetails(): ResponseEntity<Aspirants> {
        val authentication = SecurityContextHolder.getContext().authentication
        val username = authentication.name
        logger.info("AspirantsController: Solicitud GET /aspirants/me recibida para usuario: {}", username)

        val aspirant = aspirantsService.findByUsername(username)
            ?: run {
                logger.warn("AspirantsController: Aspirante no encontrado para el usuario logeado: {}", username)
                throw ResponseStatusException(HttpStatus.NOT_FOUND, "Aspirante no encontrado para el usuario logeado.")
            }

        logger.info("AspirantsController: Detalles del aspirante {} obtenidos con éxito.", username)
        return ResponseEntity(aspirant, HttpStatus.OK)
    }

    // Cambiar este método en tu AspirantsController:

    @PutMapping("/{aspirantId}/assign-career/{careerId}")
    fun assignCareer(@PathVariable aspirantId: Long, @PathVariable careerId: Long): ResponseEntity<Any> {
        logger.info(
            "AspirantsController: Solicitud PUT para asignar carrera. Aspirante ID: {}, Carrera ID: {}",
            aspirantId,
            careerId
        )

        val authentication = SecurityContextHolder.getContext().authentication
        val username = authentication.name
        val aspirantFromToken = aspirantsService.findByUsername(username)
            ?: run {
                logger.error(
                    "AspirantsController: Usuario logeado {} no encontrado en el sistema al intentar asignar carrera.",
                    username
                )
                throw ResponseStatusException(HttpStatus.FORBIDDEN, "No autorizado para esta acción.")
            }

        if (aspirantFromToken.id != aspirantId) {
            logger.warn(
                "AspirantsController: Intento de asignar carrera para ID {} por usuario {} (ID {}), pero el token es para aspirante ID {}. Acceso denegado.",
                aspirantId,
                username,
                aspirantFromToken.id,
                aspirantId
            )
            throw ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "No tienes permiso para asignar la carrera de otro aspirante."
            )
        }

        return try {
            val updatedAspirant = aspirantsService.chooseCareer(aspirantId, careerId)
            logger.info("AspirantsController: Carrera {} asignada exitosamente al aspirante {}.", careerId, aspirantId)
            ResponseEntity.ok(updatedAspirant)
        } catch (ex: ResponseStatusException) {
            logger.error(
                "AspirantsController: ERROR (ResponseStatusException) al asignar carrera al aspirante {} con carrera {}: {}",
                aspirantId,
                careerId,
                ex.message,
                ex
            )
            val status = HttpStatus.resolve(ex.statusCode.value()) ?: HttpStatus.INTERNAL_SERVER_ERROR
            ResponseEntity(mapOf("error" to (ex.reason ?: "Error al asignar carrera")), status)
        } catch (ex: Exception) {
            logger.error(
                "AspirantsController: ERROR INESPERADO al asignar carrera al aspirante {} con carrera {}: {}",
                aspirantId,
                careerId,
                ex.message,
                ex
            )
            ResponseEntity(
                mapOf("error" to "Error interno del servidor al asignar carrera."),
                HttpStatus.INTERNAL_SERVER_ERROR
            )
        }
    }
}


// Puedes eliminar o renombrar el método anterior:
// @PutMapping("/{id}/choose-career/{racingId}")
// fun chooseCareer(@PathVariable id: Long, @PathVariable racingId: Long): ResponseEntity<Any>