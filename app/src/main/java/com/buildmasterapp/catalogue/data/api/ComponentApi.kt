package com.buildmasterapp.catalogue.data.api

import com.buildmasterapp.catalogue.domain.model.Build
import com.buildmasterapp.catalogue.domain.model.BuildCreateRequest
import com.buildmasterapp.catalogue.domain.model.BuildResult
import com.buildmasterapp.catalogue.domain.model.Category
import com.buildmasterapp.catalogue.domain.model.Component
import com.buildmasterapp.catalogue.domain.model.Manufacturer
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST

interface ComponentApi {
    @GET("api/v1/catalogue")
    suspend fun getComponents(
        @retrofit2.http.Query("name") name: String? = null,
        @retrofit2.http.Query("type") type: String? = null,
        @retrofit2.http.Query("categoryId") categoryId: Long? = null,
        @retrofit2.http.Query("manufacturerId") manufacturerId: Long? = null
    ): Response<List<Component>>


    @GET("api/v1/catalogue/{id}")
    suspend fun getComponentById(@Path("id") id: Long): Response<Component>

    @GET("api/manufacturers")
    suspend fun getManufacturers(): Response<List<Manufacturer>>

    @GET("api/categories")
    suspend fun getCategories(): Response<List<Category>>

    @GET("api/builds")
    suspend fun getBuilds(): Response<List<Build>>

    @GET("api/builds/{id}")
    suspend fun getBuildById(@Path("id") id: Long): Response<Build>

    @GET("api/builds/{id}/result")
    suspend fun getBuildResult(@Path("id") id: Long): Response<BuildResult>

    @POST("api/builds")
    suspend fun createBuild(@Body buildCreateRequest: BuildCreateRequest): Response<Build>

    @DELETE("api/builds/{id}")
    suspend fun deleteBuild(@Path("id") id: Long): Response<Void>
}
