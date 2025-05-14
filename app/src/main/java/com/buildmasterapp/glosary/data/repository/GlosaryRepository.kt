package com.buildmasterapp.glosary.data.repository

import com.buildmasterapp.glosary.data.api.GlosaryApi

class GlosaryRepository(private val api: GlosaryApi) {
    suspend fun getTerm(id: String) = api.getTerm(id)
    suspend fun searchTerm(texto: String) = api.searchTerm(texto)
    suspend fun getGuide(categoria: String) = api.getGuide(categoria)
    suspend fun getTip(paso: String) = api.getTip(paso)
}