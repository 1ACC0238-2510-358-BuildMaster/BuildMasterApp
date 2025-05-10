package com.buildmasterapp.catalogue.data.api

import com.buildmasterapp.catalogue.domain.model.Component
import retrofit2.http.*
import retrofit2.Response

interface ComponentApi {
    @GET("/api/v1/catalogue")
    suspend fun getAllComponents(): Response<List<Component>>

    @POST("/api/v1/catalogue")
    suspend fun createComponent(@Body component: Component): Response<Component>
}