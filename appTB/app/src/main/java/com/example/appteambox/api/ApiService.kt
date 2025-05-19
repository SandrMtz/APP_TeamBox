package com.example.appteambox.api

import com.example.appteambox.model.Boxeador
import com.example.appteambox.model.FavoritoRequest
import com.example.appteambox.model.FiltrosBusqueda
import com.example.appteambox.model.LoginRequest
import com.example.appteambox.model.RegistroUsuario
import com.example.appteambox.model.UsuarioResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @POST("Usuarios/Crear") // Registro
    fun registrarUsuario(@Body usuario: RegistroUsuario): Call<Unit>

    @POST("Usuarios/Login") // Login
    suspend fun login(@Body request: LoginRequest): Response<UsuarioResponse>

    @GET("Usuarios/Obtener/{email}")
    suspend fun obtenerUsuarioPorEmail(@Path("email") email: String): Response<UsuarioResponse>

    @GET("Usuarios/ObtenerPorId/{id_usuario}")
    suspend fun obtenerUsuarioPorId(
        @Path("id_usuario") idUsuario: Int
    ): Response<UsuarioResponse>


    // Respecto a pantalla de Equipo

    @GET("Boxeadores/Club/{clubId}")
    suspend fun getBoxeadoresPorClub(@Path("clubId") clubId: Int): List<Boxeador>

    @POST("Boxeadores/Crear")
    suspend fun crearBoxeador(@Body boxeador: Boxeador): Response<Unit>

    @PUT("Boxeadores/{id}")
    suspend fun editarBoxeador(
        @Path("id") id: Int,
        @Body boxeador: Boxeador
    ): Response<Void>

    @DELETE("Boxeadores/{id}")
    suspend fun eliminarBoxeador(@Path("id") id: Int): Response<Void>

    @GET("Boxeadores/DniExiste")
    suspend fun dniExiste(@Query("dni_boxeador") dni: String): Boolean

    @POST("Boxeadores/Busqueda")
    suspend fun buscarBoxeadores(@Body filtros: FiltrosBusqueda): List<Boxeador>

    // Añade a favoritos desde pantalla búsqueda para imprimirla en favoritos
    @POST("Boxeadores/AnadirFavoritos")
    suspend fun agregarFavoritos(@Body favoritos: List<FavoritoRequest>): Response<Unit>

    // Obtiene favoritos desde pantalla búsqueda para que se impriman en favoritos
    @GET("Boxeadores/ObtenerFavoritos/{club_id}")
    suspend fun obtenerFavoritos(@Path("club_id") clubId: Int): List<Boxeador>

    // Elimina favoritos
    @DELETE("Boxeadores/EliminarFavoritos/{club_id}/{boxeador_id}")
    suspend fun eliminarFavorito(
        @Path("club_id") clubId: Int,
        @Path("boxeador_id") boxeadorId: Int
    ): Response<Unit>




}

