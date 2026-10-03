package com.vittiq.android.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.vittiq.android.data.model.CurrencyRate
import com.vittiq.android.data.model.UserProfile
import com.vittiq.android.data.repository.VittiqRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ProfileUiState(
    val userProfile: UserProfile? = null,
    val currencyRates: List<CurrencyRate> = emptyList()
)

class ProfileViewModel(
    private val repository: VittiqRepository
) : ViewModel() {

    val uiState: StateFlow<ProfileUiState> = combine(
        repository.getUserProfile(),
        repository.getAllCurrencyRates()
    ) { profile, rates ->
        ProfileUiState(
            userProfile = profile,
            currencyRates = rates
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProfileUiState()
    )

    fun updateUserProfile(profile: UserProfile) {
        viewModelScope.launch {
            repository.updateUserProfile(profile)
        }
    }

    fun updateCurrencyRate(rate: CurrencyRate) {
        viewModelScope.launch {
            repository.updateCurrencyRate(rate)
        }
    }

    class Factory(private val repository: VittiqRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ProfileViewModel(repository) as T
        }
    }
}
