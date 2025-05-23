package com.example.appteambox.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClient {
    // URL base de la API
    private const val BASE_URL = "http://obkserver.duckdns.org:8001/apiTeamBox/"

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) //usa Gson para convertir a JSON
            .build()
            .create(ApiService::class.java)
    }
}
