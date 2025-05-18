package com.example.appteambox.model

import com.google.gson.annotations.SerializedName

// Esta clase representa la respuesta que el servidor envía después de realizar
// una solicitud relacionada con un usuario en la API.
// todos los campos que tienen una ? pueden ser null


data class UsuarioResponse(
    @SerializedName("id")
    val id: Int = 0,
    val nombre: String,
    val apellido: String,
    val email: String,
    @SerializedName("esClub")
    val es_club: Boolean = false,
    @SerializedName("esPromotor")
    val es_promotor: Boolean = false,
    @SerializedName("esBoxeador")
    val es_boxeador: Boolean = false,
    val nombre_club: String? = null,
    val logo_club: String? = null,
    val nombre_promotora: String? = null,
    val logo_promotora: String? = null,
    val foto_perfil: String? = null,
    val fecha_creacion: String? = null,
    val comunidad: String? = null,
    val provincia: String? = null,
    val telefono1: String? = null,
    val telefono2: String? = null,
    val telefono3: String? = null,

) {

}
