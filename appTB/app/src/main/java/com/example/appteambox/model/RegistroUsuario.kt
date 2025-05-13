package com.example.appteambox.model

data class RegistroUsuario(
    val nombre: String,
    val apellido: String,
    val email: String,
    val contrasena: String,
    val comunidad: String,
    val provincia: String,
    val telefonos: List<String>,
    val es_club: Boolean,
    val es_promotor: Boolean,
    val es_boxeador: Boolean = false,
    val nombre_club: String? = null,
    val logo_club: String? = null,
    val nombre_promotora: String? = null,
    val logo_promotora: String? = null,
    val foto_perfil: String? = null
)
