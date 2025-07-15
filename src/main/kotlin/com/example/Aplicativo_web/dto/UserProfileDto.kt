package com.example.Aplicativo_web.dto

import com.example.Aplicativo_web.dto.RacingRequestDTO // Importa RacingRequestDTO

data class UserProfileDto(
    val id: Long,
    val username: String,
    val email: String,
    val roles: List<String>,
    val photoUrl: String? = null,
    val assignedRacings: List<RacingRequestDTO>? = null // AHORA USA RacingRequestDTO
)
