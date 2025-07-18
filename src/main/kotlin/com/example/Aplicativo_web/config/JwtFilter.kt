package com.example.Aplicativo_web.config

import com.example.Aplicativo_web.service.UsersAdminService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtFilter(
    private val jwtUtil: JwtUtil,
    private val usersAdminService: UsersAdminService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val path = request.requestURI
        val method = request.method

        val publicGetPaths = listOf(
            "/racing/public"
        )

        // LOGGING ANTES DE CUALQUIER LÓGICA DE FILTRO
        println("--- JwtFilter: Inicio de doFilterInternal para $method $path ---")
        println("JwtFilter: Estado inicial del SecurityContextHolder: ${SecurityContextHolder.getContext().authentication}")

        if (method == "GET" && publicGetPaths.any { path.startsWith(it) }) {
            println("JwtFilter: Ruta pública detectada ($method $path), saltando validación JWT")
            filterChain.doFilter(request, response)
            println("--- JwtFilter: Fin de doFilterInternal para $method $path (Ruta pública) ---")
            return
        }

        val authHeader = request.getHeader(HttpHeaders.AUTHORIZATION)

        println("JwtFilter: Recibida solicitud para $path")
        println("JwtFilter: Valor de Authorization Header: $authHeader")

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            println("JwtFilter: Header de autorización nulo o no empieza con Bearer. Pasando a la siguiente fase de la cadena de filtros.")
            filterChain.doFilter(request, response)
            println("--- JwtFilter: Fin de doFilterInternal para $method $path (No Bearer Token) ---")
            return
        }

        val token = authHeader.substring(7).trim()

        println("JwtFilter: Token extraído: $token")

        if (!jwtUtil.isValid(token)) {
            println("JwtFilter: Token JWT no válido. Pasando a la siguiente fase de la cadena de filtros.")
            filterChain.doFilter(request, response)
            println("--- JwtFilter: Fin de doFilterInternal para $method $path (Token Inválido) ---")
            return
        }

        val username = jwtUtil.getUsername(token)
        val roles = jwtUtil.getRoles(token)

        println("JwtFilter: Token válido. Username: $username, Roles: $roles")

        // Asegúrate de que el username no sea nulo y que no haya ya una autenticación para evitar sobrescribir
        if (username != null && SecurityContextHolder.getContext().authentication == null) {
            val authorities = roles.map { role -> SimpleGrantedAuthority(role) } // Aquí se crea SimpleGrantedAuthority("ROLE_ASPIRANT")

            val authToken = UsernamePasswordAuthenticationToken(
                username,
                null, // No necesitamos la contraseña en el contexto de seguridad después de la autenticación JWT
                authorities
            )

            authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
            SecurityContextHolder.getContext().authentication = authToken
            println("JwtFilter: Autenticación establecida en SecurityContextHolder para $username con roles $authorities")
        } else if (username == null) {
            println("JwtFilter: Username del token es nulo. No se puede autenticar.")
        } else if (SecurityContextHolder.getContext().authentication != null) {
            println("JwtFilter: Ya hay una autenticación en el contexto: ${SecurityContextHolder.getContext().authentication}. No se sobrescribe.")
        }

        // LOGGING ANTES DE CONTINUAR LA CADENA DE FILTROS
        println("JwtFilter: Estado final del SecurityContextHolder antes de filterChain.doFilter: ${SecurityContextHolder.getContext().authentication}")
        filterChain.doFilter(request, response)
        println("--- JwtFilter: Fin de doFilterInternal para $method $path (Completado) ---")
    }
}