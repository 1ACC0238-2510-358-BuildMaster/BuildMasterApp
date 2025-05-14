package com.buildmasterapp.glosary.data.api

import com.buildmasterapp.glosary.data.model.ContextTipDTO
import com.buildmasterapp.glosary.data.model.GlosaryTermDTO
import com.buildmasterapp.glosary.data.model.GuideDTO
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GlosaryApi {
    @GET("/api/glosary/termino/{id}")
    suspend fun getTerm(@Path("id") id: String): GlosaryTermDTO

    @GET("/api/glosary/buscar")
    suspend fun searchTerm(@Query("texto") texto: String): GlosaryTermDTO?

    @GET("/api/glosary/guia/{categoria}")
    suspend fun getGuide(@Path("categoria") categoria: String): GuideDTO

    @GET("/api/glosary/tip/{paso}")
    suspend fun getTip(@Path("paso") paso: String): ContextTipDTO
}