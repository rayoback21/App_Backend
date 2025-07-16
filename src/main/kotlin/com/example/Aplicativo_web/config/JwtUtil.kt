package com.example.Aplicativo_web.config

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.example.Aplicativo_web.repository.UsersRepository
import org.springframework.stereotype.Component
import java.util.Date
import java.util.concurrent.TimeUnit

@Component
class JwtUtil(
    // Inyección por constructor: Spring inyectará UsersRepository aquí
    private val usersRepository: UsersRepository
) {
    private val SECRET_KEY = "s3cr37"
    private val ALGORITHM: Algorithm = Algorithm.HMAC256(SECRET_KEY)

    fun create(username: String?): String? {
        val userEntity = usersRepository.findByUsername(username ?: throw IllegalArgumentException("Username cannot be null"))
            ?: throw IllegalArgumentException("User with username '$username' not found.")

        val rolesArray: Array<String> = userEntity.roles
            .mapNotNull { it.roles }
            .toTypedArray()

        val subjectUsername = userEntity.username ?: throw IllegalStateException("Username in UsersEntity cannot be null.")

        return JWT.create()
            .withArrayClaim("roles", rolesArray)
            .withSubject(subjectUsername)
            .withClaim("userId", userEntity.id)
            .withIssuer("app-admin")
            .withIssuedAt(Date())
            .withExpiresAt(Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(2)))
            .sign(ALGORITHM)
    }
    fun createForAspirant(username: String, aspirantId: Long, roles: List<String>): String {
        return JWT.create()
            .withClaim("aspirantId", aspirantId)
            .withArrayClaim("roles", roles.toTypedArray())
            .withSubject(username)
            .withIssuer("app-admin")
            .withIssuedAt(Date())
            // 🔁 Cambiado: duración del token de 15 días → 2 horas
            .withExpiresAt(Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(2)))
            .sign(ALGORITHM)
    }

    fun isValid(jwt: String?): Boolean {
        return try {
            JWT.require(ALGORITHM)
                .build()
                .verify(jwt)
            true
        } catch (e: JWTVerificationException) {
            false
        }
    }

    fun getUsername(jwt: String?): String? {
        return JWT.require(ALGORITHM)
            .build()
            .verify(jwt)
            .subject
    }

    fun getRoles(token: String): List<String> {
        return JWT.require(ALGORITHM)
            .build()
            .verify(token)
            .getClaim("roles")
            .asList(String::class.java)
    }

    fun getAspirantIdFromJwt(jwt: String?): Long? {
        return try {
            val decodedJWT = JWT.require(ALGORITHM).build().verify(jwt)
            decodedJWT.getClaim("aspirantId").asLong()
        } catch (e: JWTVerificationException) {
            null
        } catch (e: Exception) {
            null
        }
    }

    fun getUserIdFromJwt(jwt: String?): Long? {
        return try {
            val decodedJWT = JWT.require(ALGORITHM).build().verify(jwt)
            decodedJWT.getClaim("userId").asLong()
        } catch (e: JWTVerificationException) {
            null
        } catch (e: Exception) {
            null
        }
    }
}