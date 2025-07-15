package com.example.Aplicativo_web.dto

data class RegisterDto( // <--- Cambia 'class' por 'data class'
    var username: String? = null,
    var password: String? = null,
    var email: String? = null,
    var roles: List<String>? = null, // <--- Ahora debería resaltarse correctamente
    var photoUrl: String? = null     // <--- Y este también
)