package com.buildmasterapp.catalogue.data.api

import com.buildmasterapp.catalogue.domain.model.Category
import com.buildmasterapp.catalogue.domain.model.Component
import com.buildmasterapp.catalogue.domain.model.Manufacturer
import com.buildmasterapp.catalogue.viewmodels.ComponentViewModel.ComponentCreateRequest
import com.buildmasterapp.catalogue.viewmodels.ComponentViewModel.ComponentUpdateRequest
import retrofit2.http.*
import retrofit2.Response

interface ComponentApi {
    @GET("/api/v1/catalogue")
    suspend fun getAllComponents(): Response<List<Component>>

    @POST("/api/v1/catalogue")
    suspend fun createComponent(@Body componentCreateRequest: ComponentCreateRequest): Response<Component>

    @PUT("/api/v1/catalogue/{id}")
    suspend fun updateComponent(
        @Path("id") id: Long,
        @Body request: ComponentUpdateRequest
    ): Response<Component>

    @DELETE("/api/v1/catalogue/{id}")
    suspend fun deleteComponent(@Path("id") id: Long): Response<Void>

    @GET("/api/catalogue/manufacturers")
    suspend fun getManufacturers(): Response<List<Manufacturer>>

    @GET("/api/catalogue/categories")
    suspend fun getCategories(): Response<List<Category>>
}