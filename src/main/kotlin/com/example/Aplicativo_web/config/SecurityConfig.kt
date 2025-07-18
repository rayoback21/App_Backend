package com.example.Aplicativo_web.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

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
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .requestMatchers("/auth/**").permitAll()
                    .requestMatchers(HttpMethod.POST, "/aspirants/register").permitAll()
                    .requestMatchers(HttpMethod.POST, "/aspirants/login").permitAll()
                    .requestMatchers(HttpMethod.GET, "/racing/public").permitAll()

                    .requestMatchers(HttpMethod.GET, "/racing").hasAnyAuthority("ROLE_ASPIRANT", "profesor", "super_admin")
                    .requestMatchers(HttpMethod.POST, "/racing").hasAnyAuthority("super_admin", "admin")
                    .requestMatchers(HttpMethod.PUT, "/racing/{id}").hasAnyAuthority("super_admin", "admin")
                    .requestMatchers(HttpMethod.DELETE, "/racing/{id}").hasAnyAuthority("super_admin", "admin")

                    .requestMatchers(HttpMethod.POST, "/questions/general").hasAuthority("super_admin")
                    .requestMatchers(HttpMethod.POST, "/questions/specific").hasAnyAuthority("profesor", "super_admin")
                    .requestMatchers(HttpMethod.POST, "/questions").hasAnyAuthority("profesor", "super_admin")
                    .requestMatchers(HttpMethod.PUT, "/questions/{id}").hasAnyAuthority("profesor", "super_admin")
                    .requestMatchers(HttpMethod.DELETE, "/questions/{id}").hasAnyAuthority("profesor", "super_admin")
                    .requestMatchers(HttpMethod.GET, "/questions/**").hasAnyAuthority("profesor", "super_admin", "ROLE_ASPIRANT")

                    .requestMatchers(HttpMethod.GET, "/aspirants").hasAnyAuthority("admin", "super_admin")
                    .requestMatchers(HttpMethod.GET, "/aspirants/me").hasAuthority("ROLE_ASPIRANT")
                    .requestMatchers(HttpMethod.GET, "/aspirants/{id}").hasAnyAuthority("admin", "super_admin", "ROLE_ASPIRANT")
                    .requestMatchers(HttpMethod.PUT, "/aspirants/{id}").hasAnyAuthority("admin", "super_admin", "ROLE_ASPIRANT")
                    .requestMatchers(HttpMethod.PATCH, "/aspirants/{id}").hasAnyAuthority("admin", "super_admin", "ROLE_ASPIRANT")
                    .requestMatchers(HttpMethod.DELETE, "/aspirants/{id}").hasAnyAuthority("admin", "super_admin")
                    .requestMatchers(HttpMethod.PUT, "/aspirants/{aspirantId}/assign-career/{careerId}").hasAuthority("ROLE_ASPIRANT")
                    .requestMatchers(HttpMethod.POST, "/aspirants/upload-photo/{id}").hasAuthority("ROLE_ASPIRANT")

                    .requestMatchers(HttpMethod.POST, "/answer/**").hasAuthority("ROLE_ASPIRANT")
                    .requestMatchers(HttpMethod.GET, "/answer/**").hasAnyAuthority("profesor", "super_admin", "ROLE_ASPIRANT")
                    .requestMatchers(HttpMethod.PUT, "/answer/**").hasAnyAuthority("profesor", "super_admin")
                    .requestMatchers(HttpMethod.DELETE, "/answer/**").hasAnyAuthority("profesor", "super_admin")

                    .requestMatchers("/import/**").hasAnyAuthority("profesor", "super_admin")
                    .requestMatchers("/results/**").hasAnyAuthority("ROLE_ASPIRANT", "profesor", "super_admin")

                    .requestMatchers("/api/users/profile").authenticated()
                    .requestMatchers(HttpMethod.GET, "/api/users/all").hasAuthority("super_admin")
                    .requestMatchers(HttpMethod.POST, "/api/users/upload-photo/{userId}").hasAnyAuthority("super_admin", "admin", "profesor")
                    .requestMatchers("/uploads/photos/**").permitAll()

                    .anyRequest().denyAll()
            }
            .exceptionHandling {
                it.authenticationEntryPoint(HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
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
