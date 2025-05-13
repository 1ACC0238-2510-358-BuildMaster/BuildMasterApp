package com.buildmasterapp.catalogue.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    //private const val BASE_URL = "https://backend-041m.onrender.com/swagger-ui/index.html/"
    private const val BASE_URL = "https://backend-041m.onrender.com/"
    val instance: ComponentApi by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ComponentApi::class.java)
    }
}