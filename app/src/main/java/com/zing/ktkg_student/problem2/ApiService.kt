package com.zing.ktkg_student.problem2

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ApiService {
    @GET("api/unknown")
    suspend fun getResources(): ResourceResponse
}

object RetrofitClient {
    private const val BASE_URL = "https://reqres.in/"
    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
