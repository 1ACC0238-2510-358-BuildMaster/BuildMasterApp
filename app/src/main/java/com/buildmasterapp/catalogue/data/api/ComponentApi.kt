package com.buildmasterapp.catalogue.data.api

import com.buildmasterapp.catalogue.domain.model.Component
import retrofit2.http.*
import retrofit2.Call

interface ComponentApi {

    @GET("/api/v1/catalogue")
    fun getAllComponents(): Call<List<Component>>

    @POST("/api/v1/catalogue")
    fun createComponent(@Body component: Component): Call<Component>
}