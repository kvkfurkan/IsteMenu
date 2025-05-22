package dev.furkankavak.istemenu.model

data class AuthResponse(
    val success: Boolean,
    val message: String,
    val data: Data?
)

data class Data(
    val user: User,
    val access_token: String,
)

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val created_at:String
)