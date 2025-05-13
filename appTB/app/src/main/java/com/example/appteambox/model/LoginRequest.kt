
package com.example.appteambox.model


// Esta clase representa la solicitud de inicio de sesión que se enviará al servidor.

data class LoginRequest(
    val email: String,
    val contrasena: String
)
