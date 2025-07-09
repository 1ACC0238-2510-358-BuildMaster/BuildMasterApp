package com.buildmasterapp.user.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buildmasterapp.user.data.ProfileRepository
import com.buildmasterapp.user.data.UserProfileDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ProfileUiState {
    object Loading : ProfileUiState()
    data class Success(val profile: UserProfileDto) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

class ProfileViewModel(
    private val repository: ProfileRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun loadProfile(userId: Int) {
        _uiState.value = ProfileUiState.Loading
        viewModelScope.launch {
            val result = repository.getProfile(userId)
            _uiState.value = result.fold(
                onSuccess = { ProfileUiState.Success(it) },
                onFailure = { ProfileUiState.Error(it.localizedMessage ?: "Error desconocido") }
            )
        }
    }

    fun loadMe() {
        _uiState.value = ProfileUiState.Loading
        viewModelScope.launch {
            val result = repository.getMe()
            _uiState.value = result.fold(
                onSuccess = { ProfileUiState.Success(it) },
                onFailure = { ProfileUiState.Error(it.localizedMessage ?: "Error desconocido") }
            )
        }
    }
}
