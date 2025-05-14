package com.buildmasterapp.glosary.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buildmasterapp.glosary.data.model.ContextTipDTO
import com.buildmasterapp.glosary.data.model.GlosaryTermDTO
import com.buildmasterapp.glosary.data.model.GuideDTO
import com.buildmasterapp.glosary.data.repository.GlosaryRepository
import kotlinx.coroutines.launch

class GlosaryViewModel(private val repo: GlosaryRepository) : ViewModel() {
    var termino by mutableStateOf<GlosaryTermDTO?>(null)
    var guia by mutableStateOf<GuideDTO?>(null)
    var tip by mutableStateOf<ContextTipDTO?>(null)

    fun loadTerm(id: String) {
        viewModelScope.launch {
            termino = repo.getTerm(id)
        }
    }

    fun search(texto: String) {
        viewModelScope.launch {
            termino = repo.searchTerm(texto)
        }
    }

    fun loadGuide(categoria: String) {
        viewModelScope.launch {
            guia = repo.getGuide(categoria)
        }
    }

    fun loadTip(paso: String) {
        viewModelScope.launch {
            tip = repo.getTip(paso)
        }
    }
}