package com.example.Aplicativo_web.dto

data class AspirantsRegisterDto(
    var name: String,
    var lastName: String,
    var nui: String,
    var username: String?,
    var email: String,
    var password: String,
    var racingId: Long? = null
)
