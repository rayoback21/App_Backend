package com.example.Aplicativo_web.service

import com.example.Aplicativo_web.config.JwtUtil
import com.example.Aplicativo_web.entity.Aspirants
import com.example.Aplicativo_web.entity.Racing
import com.example.Aplicativo_web.repository.AspirantsRepository
import com.example.Aplicativo_web.repository.RacingRepository // Asegúrate de importar RacingRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class AspirantsService {

    @Autowired
    lateinit var aspirantsRepository: AspirantsRepository

    @Autowired
    lateinit var jwtUtil: JwtUtil

    @Autowired
    lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    lateinit var racingRepository: RacingRepository // Inyectar RacingRepository

    fun list(): List<Aspirants> {
        return aspirantsRepository.findAll()
    }

    fun save(aspirants: Aspirants): Aspirants {
        if (aspirants.username.isNullOrBlank()) {
            aspirants.username = generateUsername(aspirants)
        }
        // La contraseña ya debe venir encriptada si se registra a través de un DTO que la encripta,
        // o se encripta aquí si el DTO la trae en texto plano.
        // Tu controller envía el DTO con password en texto plano, así que la encriptación aquí es correcta.
        aspirants.password = passwordEncoder.encode(aspirants.password)
        return aspirantsRepository.save(aspirants)
    }

    fun update(aspirants: Aspirants): Aspirants {
        val id = aspirants.id ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "El id del aspirante es requerido")
        try {
            // No encriptar la contraseña si se actualiza solo el perfil y no la contraseña
            // Si la contraseña se actualiza, debería pasar por passwordEncoder.encode()
            aspirantsRepository.findById(id).orElseThrow { Exception("El aspirante no existe para actualizar") }
            return aspirantsRepository.save(aspirants)
        } catch (ex: Exception) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ex.message)
        }
    }

    fun updateName(aspirants: Aspirants): Aspirants {
        val id = aspirants.id ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "El id del aspirante es requerido")
        val existingAspirants = aspirantsRepository.findById(id)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Aspirante con id $id no encontrado") }
        existingAspirants.name = aspirants.name
        return aspirantsRepository.save(existingAspirants)
    }

    fun delete(id: Long?) {
        val idNotNull = id ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "El id es requerido")
        val aspirants = aspirantsRepository.findById(idNotNull)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Estudiante no existe con el Id: $idNotNull") }
        aspirantsRepository.delete(aspirants)
    }

    fun findByRacingId(racingId: Long): List<Aspirants> {
        return aspirantsRepository.findByRacingId(racingId)
    }

    fun findById(id: Long?): Aspirants? {
        val idNotNull = id ?: return null
        return aspirantsRepository.findById(idNotNull).orElse(null)
    }

    fun findByNui(nui: String): Aspirants? {
        return aspirantsRepository.findByNui(nui)
    }

    fun findByUsername(username: String?): Aspirants? {
        if (username.isNullOrBlank()) return null
        return aspirantsRepository.findByUsername(username)
    }

    private fun generateUsername(aspirants: Aspirants): String {
        val base = "${aspirants.name?.lowercase() ?: "asp"}${aspirants.lastName?.lowercase()?.take(4) ?: "user"}"
        val random = (100..999).random()
        return "$base$random"
    }

    // LOGIN con debug para verificar password
    fun login(username: String, password: String): String {
        val aspirant = aspirantsRepository.findByUsername(username)
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales incorrectas")

        println("Password login: $password")                      // Debug
        println("Password en BD: ${aspirant.password}")            // Debug
        val matches = passwordEncoder.matches(password, aspirant.password)
        println("Coincide contraseña? $matches")                   // Debug

        if (!matches) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales incorrectas")
        }

        // Si las credenciales son correctas, generar el token con el ID del aspirante y el rol
        return jwtUtil.createForAspirant(
            username = aspirant.username!!,
            aspirantId = aspirant.id!!,
            roles = listOf("ROLE_ASPIRANT") // Asegúrate que este rol coincida con tu SecurityConfig y Frontend
        )
    }

    // Método para que el aspirante elija su carrera (Racing)
    fun chooseCareer(aspirantId: Long, racingId: Long): Aspirants {
        val aspirant = aspirantsRepository.findById(aspirantId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Aspirante no encontrado") }

        val racing = racingRepository.findById(racingId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Carrera no encontrada") }

        aspirant.racing = racing // Asigna la carrera al aspirante
        return aspirantsRepository.save(aspirant)
    }
}
