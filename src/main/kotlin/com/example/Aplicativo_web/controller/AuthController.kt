package com.example.Aplicativo_web.controller

import com.example.Aplicativo_web.dto.LoginDto
import com.example.Aplicativo_web.dto.RegisterDto
import com.example.Aplicativo_web.dto.TokenDto
import com.example.Aplicativo_web.config.JwtUtil
import com.example.Aplicativo_web.service.UsersAdminService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(
    private val usersAdminService: UsersAdminService,
    private val authenticationManager: AuthenticationManager,
    private val jwtUtil: JwtUtil
) {

    @PostMapping("/login")
    fun login(@RequestBody loginDto: LoginDto): ResponseEntity<TokenDto> {
        val login = UsernamePasswordAuthenticationToken(loginDto.username, loginDto.password)
        val authentication: Authentication = authenticationManager.authenticate(login)
        val response = TokenDto().apply { jwt = jwtUtil.create(loginDto.username) }
        return ResponseEntity(response, HttpStatus.OK)
    }

    @PostMapping("/register")
    fun register(@RequestBody registerDto: RegisterDto): ResponseEntity<*> {
        val response = usersAdminService.register(registerDto)
        return ResponseEntity(response, HttpStatus.OK)
    }
}
