package com.example.Aplicativo_web.config

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
// import org.springframework.core.annotation.Order // <-- ¡Eliminar esta importación!

@Component
// @Order(1) // <-- ¡Eliminar esta línea!
class JwtFilter(
    private val jwtUtil: JwtUtil,
    private val userDetailsService: UserDetailsService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader(HttpHeaders.AUTHORIZATION)

        println("JwtFilter: Recibida solicitud para ${request.requestURI}")
        println("JwtFilter: Valor de Authorization Header: $authHeader")

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            println("JwtFilter: Header de autorización nulo o no empieza con Bearer. Pasando a la siguiente fase.")
            filterChain.doFilter(request, response)
            return
        }

        val token = authHeader.substring(7).trim()

        println("JwtFilter: Token extraído: $token")

        if (!jwtUtil.isValid(token)) {
            println("JwtFilter: Token JWT no válido. Pasando a la siguiente fase.")
            filterChain.doFilter(request, response)
            return
        }

        val username = jwtUtil.getUsername(token)
        val roles = jwtUtil.getRoles(token)

        println("JwtFilter: Token válido. Username: $username, Roles: $roles")

        // Esta condición es importante para no sobrescribir una autenticación existente
        // (aunque en tu caso, el problema es que se está borrando después)
        if (username != null && SecurityContextHolder.getContext().authentication == null) {
            val authorities = roles.map { role -> SimpleGrantedAuthority(role) }

            val authToken = UsernamePasswordAuthenticationToken(
                username,
                null,
                authorities
            )

            authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
            SecurityContextHolder.getContext().authentication = authToken
            println("JwtFilter: Autenticación establecida en SecurityContextHolder para $username con roles $authorities")
        } else if (username == null) {
            println("JwtFilter: Username del token es nulo. No se puede autenticar.")
        } else if (SecurityContextHolder.getContext().authentication != null) {
            println("JwtFilter: Ya hay una autenticación en el contexto. No se sobrescribe.")
        }

        filterChain.doFilter(request, response)
    }
}