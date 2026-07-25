package com.example.vittiq.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.vittiq.VittiqApplication
import com.example.vittiq.data.db.UserProfile
import com.example.vittiq.data.repository.VittiqRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ProfileUiState(
    val userProfile: UserProfile? = null,
    val isLoading: Boolean = true
)

class ProfileViewModel(private val repository: VittiqRepository) : ViewModel() {

    val uiState: StateFlow<ProfileUiState> = repository.userProfileFlow.map { profile ->
        ProfileUiState(userProfile = profile, isLoading = false)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProfileUiState()
    )

    fun updateProfile(username: String, fullName: String, photoUri: String?) {
        viewModelScope.launch {
            val current = uiState.value.userProfile
            if (current != null) {
                val updated = current.copy(
                    username = username,
                    fullName = fullName,
                    photoUri = photoUri
                )
                repository.updateUserProfile(updated)
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as VittiqApplication
                ProfileViewModel(application.container.repository)
            }
        }
    }
}
