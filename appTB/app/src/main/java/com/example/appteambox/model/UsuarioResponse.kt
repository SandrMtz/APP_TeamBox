package com.example.appteambox.model

// Esta clase representa la respuesta que el servidor envía después de realizar
// una solicitud relacionada con un usuario en la API.
// todos los campos que tienen una ? pueden ser null


data class UsuarioResponse(
    val id: Int,
    val nombre: String,
    val apellido: String,
    val email: String,
    val esClub: Boolean,
    val esPromotor: Boolean,
    val esBoxeador: Boolean,
    val nombre_club: String?,
    val logoClub: String?,
    val nombre_promotora: String?,
    val logoPromotora: String?,
    val fotoPerfil: String?,
    val fecha_creacion: String?,
    val foto_perfil: String?

)
