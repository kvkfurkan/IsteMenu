package dev.furkankavak.istemenu.model

data class SignUpRequest(
    val name: String,
    val email: String,
    val password: String,
    val password_confirmation: String
)
