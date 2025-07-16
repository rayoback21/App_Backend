package com.example.Aplicativo_web.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(
    private val jwtFilter: JwtFilter
) {
    @Bean
    @Throws(Exception::class)
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .cors { }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth ->
                auth
                    // Permitir OPTIONS en todas las rutas (preflight CORS)
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                    // Endpoints públicos y de autenticación
                    .requestMatchers("/auth/**").permitAll()
                    .requestMatchers(HttpMethod.POST, "/aspirants/register").permitAll()
                    .requestMatchers(HttpMethod.POST, "/aspirants/login").permitAll()
                    .requestMatchers(HttpMethod.GET, "/public/racings").permitAll() // Si usas esta URL, mantenla

                    // REGLA CLAVE PARA super_admin EN /questions/general
                    .requestMatchers(HttpMethod.POST, "/questions/general").hasAuthority("super_admin")

                    // **CAMBIO AQUÍ: Permitir a ROLE_ASPIRANT ver la lista de carreras**
                    // El endpoint para listar todas las carreras es GET /racing
                    .requestMatchers(HttpMethod.GET, "/racing").hasAnyAuthority("ROLE_ASPIRANT", "super_admin")
                    // Si tienes un endpoint /careers para listar, también deberías añadirlo aquí:
                    // .requestMatchers(HttpMethod.GET, "/careers").hasAnyAuthority("ROLE_ASPIRANT", "super_admin")

                    // Mantenemos la regla general para /racing/** para super_admin para otros métodos (POST, PUT, DELETE)
                    .requestMatchers("/racing/**").hasAuthority("super_admin")


                    .requestMatchers("/api/profesor/**").hasAuthority("profesor")

                    // Preguntas específicas (por carrera) - Profesor y SuperAdmin
                    .requestMatchers(HttpMethod.POST, "/questions/specific").hasAnyAuthority("profesor", "super_admin")
                    .requestMatchers(HttpMethod.POST, "/questions").hasAnyAuthority("profesor", "super_admin")
                    .requestMatchers(HttpMethod.PUT, "/questions/{id}").hasAnyAuthority("profesor", "super_admin")
                    .requestMatchers(HttpMethod.DELETE, "/questions/{id}").hasAnyAuthority("profesor", "super_admin")
                    // Y esta para ver las preguntas
                    .requestMatchers(HttpMethod.GET, "/questions/**").hasAnyAuthority("profesor", "super_admin", "ROLE_ASPIRANT")


                    // Gestión de Aspirantes
                    .requestMatchers(HttpMethod.GET, "/aspirants").hasAnyAuthority("admin", "super_admin")
                    .requestMatchers(HttpMethod.GET, "/aspirants/me").hasAuthority("ROLE_ASPIRANT")
                    .requestMatchers(HttpMethod.GET, "/aspirants/{id}").hasAnyAuthority("admin", "super_admin", "ROLE_ASPIRANT")
                    .requestMatchers(HttpMethod.PUT, "/aspirants/{id}").hasAnyAuthority("admin", "super_admin", "ROLE_ASPIRANT")
                    .requestMatchers(HttpMethod.PATCH, "/aspirants/{id}").hasAnyAuthority("admin", "super_admin", "ROLE_ASPIRANT")
                    .requestMatchers(HttpMethod.DELETE, "/aspirants/{id}").hasAnyAuthority("admin", "super_admin")

                    // Respuestas de Aspirantes
                    .requestMatchers(HttpMethod.POST, "/answer/**").hasAuthority("ROLE_ASPIRANT")
                    .requestMatchers(HttpMethod.GET, "/answer/**").hasAnyAuthority("profesor", "super_admin", "ROLE_ASPIRANT")
                    .requestMatchers(HttpMethod.PUT, "/answer/**").hasAnyAuthority("profesor", "super_admin")
                    .requestMatchers(HttpMethod.DELETE, "/answer/**").hasAnyAuthority("profesor", "super_admin")

                    // Importación y Resultados
                    .requestMatchers("/import/**").hasAnyAuthority("profesor", "super_admin")
                    .requestMatchers("/results/**").hasAnyAuthority("ROLE_ASPIRANT", "profesor", "super_admin")

                    // Perfiles de usuario y gestión de fotos
                    .requestMatchers("/api/users/profile").authenticated()
                    .requestMatchers(HttpMethod.GET, "/api/users/all").hasAuthority("super_admin")
                    .requestMatchers(HttpMethod.POST, "/api/users/upload-photo/{userId}").hasAnyAuthority("super_admin", "admin", "profesor")
                    .requestMatchers("/uploads/photos/**").permitAll()

                    // Última línea: si no coincide con ninguna de las anteriores, denegar acceso.
                    .anyRequest().denyAll()
            }
            .exceptionHandling { exceptions ->
                exceptions.authenticationEntryPoint(HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            }
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }

    @Bean
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager {
        return config.authenticationManager
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}
