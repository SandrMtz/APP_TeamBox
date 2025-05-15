package com.example.appteambox.model

data class Usuario(
    val id_usuario: Int,
    val nombre: String,
    val apellido: String,
    val email: String,
    val es_club: Boolean,
    val es_promotor: Boolean,
    val es_boxeador: Boolean,
    val nombre_club: String?,
    val logo_club: String?,
    val nombre_promotora: String?,
    val logo_promotora: String?,
    val comunidad: String,
    val provincia: String,
    val telefono1: String?,
    val telefono2: String?,
    val telefono3: String?,
    val foto_perfil: String?,
    val fecha_creacion: String
) {



}