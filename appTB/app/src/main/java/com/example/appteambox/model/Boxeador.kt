package com.example.appteambox.model

data class Boxeador(
    val Id_boxeador: Int = 0,                      // Clave primaria autoincremental
    val nombre: String,
    val apellido: String,
    val fecha_nacimiento: String,                  // Formato: "YYYY-MM-DD"
    val dni_boxeador: String,                      // DNI: 8 números + 1 letra (ej: 12345678A) Control desde aqui
    val genero: Boolean,                           // true = masculino, false = femenino
    val peso: Double?,
    val categoria: String?,                        // Categoría esto se maneja automaticamente aqui depende del año de nacimiento
    val foto_perfil: String?,                      // Imagen en Base64
    val comunidad: String,
    val provincia: String,
    val club_id: Int,                              // ID del club al que pertenece
    val fecha_registro: String
)
