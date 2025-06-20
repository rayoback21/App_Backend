package com.example.Aplicativo_web.service

import com.example.Aplicativo_web.dto.RegisterDto
import com.example.Aplicativo_web.entity.UsersEntity
import com.example.Aplicativo_web.repository.UsersRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import kotlin.jvm.Throws

@Service
class UsersAdminService: UserDetailsService {
    @Autowired
    lateinit var usersRepository: UsersRepository

    @Override
    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails? {
        val usersEntity =
            usersRepository.findByUsername(username) ?: throw UsernameNotFoundException("username $username not found")
        val roles: Array<String?> = usersEntity.roles?.map { role -> role.roles }!!.toTypedArray()
        return User.builder()
            .username(usersEntity.username)
            .password(usersEntity.password)
            .roles(*roles)
            .build()
    }
    fun register(registerDto: RegisterDto): UsersEntity{
        val user = UsersEntity()
        user.apply {
            username = registerDto.username
            password = BCryptPasswordEncoder().encode(registerDto.password)
            email = registerDto.email

        }
        usersRepository.save(user)
        return user
    }
}