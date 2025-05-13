package com.example.appteambox.api

import com.example.appteambox.model.LoginRequest
import com.example.appteambox.model.RegistroUsuario
import com.example.appteambox.model.UsuarioResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("Usuarios/Crear") // Registro
    fun registrarUsuario(@Body usuario: RegistroUsuario): Call<Unit>

    @POST("Usuarios/Login") // Login
    suspend fun login(@Body request: LoginRequest): Response<UsuarioResponse>

    @GET("Usuarios/Obtener/{email}")
    suspend fun obtenerUsuarioPorEmail(@Path("email") email: String): Response<UsuarioResponse>


}

