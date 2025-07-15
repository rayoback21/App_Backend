package com.example.Aplicativo_web.service

import com.example.Aplicativo_web.dto.RegisterDto
import com.example.Aplicativo_web.dto.UserProfileDto
import com.example.Aplicativo_web.entity.Roles
import com.example.Aplicativo_web.entity.UsersEntity
import com.example.Aplicativo_web.repository.RolesRepository
import com.example.Aplicativo_web.repository.UsersRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
class UsersAdminService : UserDetailsService {

    @Autowired
    lateinit var usersRepository: UsersRepository

    @Autowired
    lateinit var rolesRepository: RolesRepository

    override fun loadUserByUsername(username: String): UserDetails {
        println("UsersAdminService: loadUserByUsername llamado para: $username")
        if (usersRepository.count() == 0L) {
            throw IllegalStateException("No hay usuarios registrados. Cree el primer usuario desde el formulario de registro.")
        }

        val userEntity = usersRepository.findByUsername(username)
            .orElseThrow { UsernameNotFoundException("Usuario '$username' no encontrado") }

        val usernameNonNull = userEntity.username ?: throw IllegalStateException("Username no puede ser null")
        val passwordNonNull = userEntity.password ?: throw IllegalStateException("Password no puede ser null")

        val authorities = userEntity.roles
            .mapNotNull { it.roles }
            .map { "ROLE_$it" }
            .map { SimpleGrantedAuthority(it) }

        println("UsersAdminService: Roles encontrados para $username: ${authorities.joinToString { it.authority }}")

        return User(usernameNonNull, passwordNonNull, authorities)
    }

    @Transactional
    fun register(registerDto: RegisterDto): UsersEntity {
        println("UsersAdminService: register llamado para: ${registerDto.username}")
        val encodedPassword = registerDto.password?.let { BCryptPasswordEncoder().encode(it) }
            ?: throw IllegalArgumentException("Password no puede ser null")

        val user = UsersEntity().apply {
            username = registerDto.username
            password = encodedPassword
            email = registerDto.email
            photoUrl = registerDto.photoUrl
        }

        val savedUser = usersRepository.save(user)
        savedUser.roles.clear()

        registerDto.roles?.forEach { roleName ->
            val foundRoles = rolesRepository.findByRoles(roleName)
            if (foundRoles.isEmpty()) {
                throw IllegalArgumentException("Rol '$roleName' no encontrado")
            }

            val userRole = Roles().apply {
                roles = foundRoles.first().roles
                userId = savedUser.id
                userEntity = savedUser
            }

            savedUser.roles.add(userRole)
        }

        println("UsersAdminService: Usuario ${savedUser.username} registrado con roles: ${savedUser.roles.mapNotNull { it.roles }.joinToString()}")
        return usersRepository.save(savedUser)
    }

    fun findUserIdByUsername(username: String): Long {
        println("UsersAdminService: findUserIdByUsername llamado para: $username")
        val user = usersRepository.findByUsername(username)
            .orElseThrow { RuntimeException("Usuario no encontrado con username: $username") }
        return user.id ?: throw RuntimeException("Usuario sin ID")
    }

    fun findByUsername(username: String): UsersEntity? {
        println("UsersAdminService: findByUsername (para controlador) llamado para: $username")
        return usersRepository.findByUsername(username).orElse(null)
    }

    @Transactional
    fun updatePhotoUrl(userId: Long, photoUrl: String): UsersEntity {
        println("UsersAdminService: updatePhotoUrl llamado para userId: $userId con URL: $photoUrl")
        val user = usersRepository.findById(userId)
            .orElseThrow {
                println("UsersAdminService: Usuario con ID $userId no encontrado para actualizar foto.")
                ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado con ID: $userId")
            }

        user.photoUrl = photoUrl
        println("UsersAdminService: photoUrl de la entidad establecida a: ${user.photoUrl}")
        val savedUser = usersRepository.save(user)
        println("UsersAdminService: Usuario con ID $userId guardado en el repositorio. photoUrl después de guardar: ${savedUser.photoUrl}")
        return savedUser
    }

    fun listAllUsersWithRoles(): List<UserProfileDto> {
        println("UsersAdminService: listAllUsersWithRoles llamado.")
        return usersRepository.findAll().map { user ->
            UserProfileDto(
                id = user.id ?: 0L,
                username = user.username ?: "N/A",
                email = user.email ?: "N/A",
                roles = user.roles.mapNotNull { it.roles }.toList(),
                photoUrl = user.photoUrl
            )
        }
    }
}
