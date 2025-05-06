package com.example.appteambox.model
// Esta clase representa la respuesta del inicio de sesión que se envió al servidor.
data class UsuarioRespuesta(
    val id: Int,
    val nombre: String,
    val email: String,
    val esClub: Boolean,
    val esPromotor: Boolean
)
