package com.example.appteambox.model

data class FiltrosBusqueda(
    val nombre: String? = null,
    val apellido: String? = null,
    val comunidad: List<String> = emptyList(),
    val categoria: List<String> = emptyList(),
    val genero: Boolean? = null,
    val pesoMin: Double? = null,
    val pesoMax: Double? = null,
    val nombre_club: String? = null
)

