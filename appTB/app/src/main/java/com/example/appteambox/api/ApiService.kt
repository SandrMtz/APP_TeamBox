package com.example.appteambox.api

import com.example.appteambox.model.RegistroUsuario
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("Usuarios/Crear") // Ruta de la API para el registro de usuarios
    fun registrarUsuario(@Body usuario: RegistroUsuario): Call<Unit>

}
