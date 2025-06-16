package com.buildmasterapp.login.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClientLogin {
    private const val BASE_URL = "https://backend-041m.onrender.com/swagger-ui/index.html/"

    val instance: LoginApi by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(LoginApi::class.java)
    }
}