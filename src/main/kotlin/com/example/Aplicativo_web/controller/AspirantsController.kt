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

@RestController
@RequestMapping("/aspirants")
@CrossOrigin(
    origins = ["http://localhost:5173"], // Asegúrate que esta URL sea la de tu frontend
    allowedHeaders = ["*"],
    allowCredentials = "true",
    methods = [RequestMethod.GET, RequestMethod.POST, RequestMethod.PATCH, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS]
)
class AspirantsController {

    @Autowired
    lateinit var aspirantsService: AspirantsService

    @Autowired
    lateinit var jwtUtil: JwtUtil

    @Autowired
    lateinit var passwordEncoder: PasswordEncoder // Asegúrate de que esto esté inyectado si lo usas aquí

    @GetMapping
    fun list(): List<Aspirants> = aspirantsService.list()

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<Aspirants> { // Cambiado a ResponseEntity<Aspirants>
        val aspirant = aspirantsService.findById(id)
        return if (aspirant != null) ResponseEntity.ok(aspirant)
        else ResponseEntity(HttpStatus.NOT_FOUND) // Devuelve solo el estado si no se encuentra
    }

    @GetMapping("/nui/{nui}")
    fun getByNui(@PathVariable nui: String): ResponseEntity<Aspirants> { // Cambiado a ResponseEntity<Aspirants>
        val aspirant = aspirantsService.findByNui(nui)
        return if (aspirant != null) ResponseEntity.ok(aspirant)
        else ResponseEntity(HttpStatus.NOT_FOUND)
    }

    @GetMapping("/racing/{racingId}")
    fun getByRacing(@PathVariable racingId: Long): ResponseEntity<List<Aspirants>> {
        val aspirants = aspirantsService.findByRacingId(racingId)
        return ResponseEntity.ok(aspirants)
    }

    @PostMapping("/register")
    fun register(@RequestBody dto: AspirantsRegisterDto): ResponseEntity<Aspirants> {
        val aspirant = Aspirants().apply {
            name = dto.name
            lastName = dto.lastName
            username = dto.username
            nui = dto.nui
            email = dto.email
            password = dto.password // La contraseña se codificará en el servicio
            racing = null // La carrera se asigna después del registro
        }
        val saved = aspirantsService.save(aspirant)
        return ResponseEntity.ok(saved)
    }

    @PostMapping("/login")
    fun login(@RequestBody loginDto: LoginDto): ResponseEntity<Any> {
        return try {
            val token = aspirantsService.login(loginDto.username ?: "", loginDto.password ?: "")
            val aspirant = aspirantsService.findByUsername(loginDto.username)
            ResponseEntity.ok(mapOf("token" to token, "aspirant" to aspirant))
        } catch (ex: ResponseStatusException) {
            // CORRECCIÓN: Usar HttpStatus.resolve(ex.statusCode.value()) para obtener HttpStatus
            val status = HttpStatus.resolve(ex.statusCode.value()) ?: HttpStatus.INTERNAL_SERVER_ERROR
            ResponseEntity(mapOf("error" to (ex.reason ?: "Credenciales incorrectas")), status)
        } catch (ex: Exception) {
            // Capturar cualquier otra excepción inesperada
            ResponseEntity(mapOf("error" to "Error interno del servidor durante el login."), HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    // Nuevo endpoint para obtener el perfil del aspirante logeado
    @GetMapping("/me")
    fun getMyAspirantDetails(): ResponseEntity<Aspirants> {
        val authentication = SecurityContextHolder.getContext().authentication
        val username = authentication.name // Username del aspirante logeado

        val aspirant = aspirantsService.findByUsername(username)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Aspirante no encontrado para el usuario logeado.")

        return ResponseEntity(aspirant, HttpStatus.OK)
    }

    @PutMapping("/{id}/choose-career/{racingId}")
    fun chooseCareer(@PathVariable id: Long, @PathVariable racingId: Long): ResponseEntity<Any> {
        // Opcional: Verificar que el ID del aspirante en la URL coincida con el usuario logeado
        val authentication = SecurityContextHolder.getContext().authentication
        val username = authentication.name
        val aspirantFromToken = aspirantsService.findByUsername(username)
            ?: throw ResponseStatusException(HttpStatus.FORBIDDEN, "No autorizado para esta acción.")

        if (aspirantFromToken.id != id) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para asignar la carrera de otro aspirante.")
        }

        return try {
            val updatedAspirant = aspirantsService.chooseCareer(id, racingId)
            ResponseEntity.ok(updatedAspirant)
        } catch (ex: ResponseStatusException) {
            // CORRECCIÓN: Usar HttpStatus.resolve(ex.statusCode.value()) para obtener HttpStatus
            val status = HttpStatus.resolve(ex.statusCode.value()) ?: HttpStatus.INTERNAL_SERVER_ERROR
            ResponseEntity(mapOf("error" to (ex.reason ?: "Error al asignar carrera")), status)
        } catch (ex: Exception) {
            ResponseEntity(mapOf("error" to "Error interno del servidor al asignar carrera."), HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody aspirant: Aspirants): ResponseEntity<Aspirants> {
        if (id != aspirant.id) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "El ID en la URL no coincide con el ID del cuerpo.")
        }
        val updated = aspirantsService.update(aspirant)
        return ResponseEntity.ok(updated)
    }

    @PatchMapping("/{id}")
    fun patch(@PathVariable id: Long, @RequestBody aspirant: Aspirants): ResponseEntity<Aspirants> {
        val updated = aspirantsService.updateName(aspirant)
        return ResponseEntity.ok(updated)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<String> {
        aspirantsService.delete(id)
        return ResponseEntity("Aspirante eliminado", HttpStatus.OK)
    }
}
