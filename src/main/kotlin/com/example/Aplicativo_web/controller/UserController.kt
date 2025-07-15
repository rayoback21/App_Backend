package com.example.Aplicativo_web.controller

import com.example.Aplicativo_web.dto.UserProfileDto
import com.example.Aplicativo_web.dto.RacingRequestDTO // Importa este DTO para mapear las carreras
import com.example.Aplicativo_web.service.UsersAdminService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.io.IOException

@RestController
@RequestMapping("/api/users")
@CrossOrigin(
    origins = ["http://localhost:5173"],
    allowedHeaders = ["*"],
    allowCredentials = "true",
    methods = [RequestMethod.GET, RequestMethod.POST, RequestMethod.PATCH, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS]
)
class UserController {  // <--- Aquí el cambio de nombre singular

    @Autowired
    private lateinit var usersAdminService: UsersAdminService

    // Endpoint para obtener el perfil del usuario logeado
    @GetMapping("/profile")
    fun getMyProfile(): ResponseEntity<UserProfileDto> {
        val authentication = SecurityContextHolder.getContext().authentication
        val username = authentication.name

        val user = usersAdminService.findByUsername(username)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado.")

        // <--- ¡AJUSTE CLAVE AQUÍ! Mapear las carreras asignadas si el usuario es un profesor
        // Acceder a user.racings aquí forzará la carga perezosa (LAZY loading)
        val assignedRacingsDto = if (user.roles.any { it.roles == "profesor" || it.roles == "PROFESSOR" }) {
            user.racings.map { racing ->
                RacingRequestDTO(
                    id = racing.id ?: 0L,
                    career = racing.career ?: "N/A",
                    professorId = racing.professor?.id ?: 0L // Asegúrate de que professor no sea nulo
                )
            }
        } else {
            emptyList() // Si no es profesor, no tendrá carreras asignadas de esta manera
        }

        val userProfileDto = UserProfileDto(
            id = user.id ?: 0L,
            username = user.username ?: "Sin username",
            email = user.email ?: "Sin email",
            roles = user.roles.mapNotNull { it.roles }.toList(),
            photoUrl = user.photoUrl,
            assignedRacings = assignedRacingsDto // Asignar las carreras mapeadas al DTO
        )

        return ResponseEntity.ok(userProfileDto)
    }

    // Nuevo endpoint para listar todos los usuarios (solo para Super Admin)
    @GetMapping("/all")
    fun getAllUsers(): ResponseEntity<List<UserProfileDto>> {
        val users = usersAdminService.listAllUsersWithRoles()
        return ResponseEntity.ok(users)
    }

    // Endpoint para subir foto de perfil
    @PostMapping("/upload-photo/{userId}")
    fun uploadPhoto(@PathVariable userId: Long, @RequestParam("file") file: MultipartFile): ResponseEntity<UserProfileDto> {
        val authentication = SecurityContextHolder.getContext().authentication
        val loggedInUsername = authentication.name
        val loggedInUser = usersAdminService.findByUsername(loggedInUsername)
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado.")

        val isSuperAdmin = loggedInUser.roles.any { it.roles == "super_admin" || it.roles == "SUPER_ADMIN" }

        if (loggedInUser.id != userId && !isSuperAdmin) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para subir la foto de otro usuario.")
        }

        if (file.isEmpty) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "El archivo de la foto está vacío.")
        }

        try {
            val uploadDir = Paths.get("uploads/photos")
            Files.createDirectories(uploadDir)

            val fileExtension = file.originalFilename?.substringAfterLast('.', "")
            val fileName = "${userId}_${System.currentTimeMillis()}.${fileExtension}"
            val filePath = uploadDir.resolve(fileName)

            Files.copy(file.inputStream, filePath, StandardCopyOption.REPLACE_EXISTING)

            val photoUrlForDb = "/uploads/photos/$fileName"
            val updatedUser = usersAdminService.updatePhotoUrl(userId, photoUrlForDb)

            // Re-obtener el perfil completo para devolverlo actualizado, incluyendo carreras si aplica
            val updatedUserProfileDto = UserProfileDto(
                id = updatedUser.id ?: 0L,
                username = updatedUser.username ?: "Sin username",
                email = updatedUser.email ?: "Sin email",
                roles = updatedUser.roles.mapNotNull { it.roles }.toList(),
                photoUrl = updatedUser.photoUrl,
                // <--- ¡AJUSTE CLAVE AQUÍ! Asegurarse de que las carreras también se actualicen en la respuesta
                assignedRacings = if (updatedUser.roles.any { it.roles == "profesor" || it.roles == "PROFESSOR" }) {
                    updatedUser.racings.map { racing ->
                        RacingRequestDTO(
                            id = racing.id ?: 0L,
                            career = racing.career ?: "N/A",
                            professorId = racing.professor?.id ?: 0L
                        )
                    }
                } else {
                    emptyList()
                }
            )
            return ResponseEntity.ok(updatedUserProfileDto)

        } catch (e: IOException) {
            e.printStackTrace()
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error de E/S al subir la foto: ${e.message}")
        } catch (e: Exception) {
            e.printStackTrace()
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error inesperado al subir la foto: ${e.message}")
        }
    }
}
