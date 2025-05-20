package com.example.appteambox.model

data class FiltrosBusqueda(
    val nombre_o_apellido: String? = null,
    val peso_min: Double? = null,
    val peso_max: Double? = null,
    val comunidades: List<String> = emptyList(),
    val categorias: List<String> = emptyList(),
    val genero: Boolean? = null,
    val nombre_club: String? = null
)


