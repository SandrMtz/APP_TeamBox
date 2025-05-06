package com.example.appteambox.api

import com.example.appteambox.model.LoginRequest
import com.example.appteambox.model.RegistroUsuario
import com.example.appteambox.model.UsuarioRespuesta
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("Usuarios/Crear") // Registro
    fun registrarUsuario(@Body usuario: RegistroUsuario): Call<Unit>

    @POST("Usuarios/Login") // Login
    suspend fun login(@Body request: LoginRequest): Response<UsuarioRespuesta>
}
