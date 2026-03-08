package com.example.timetrackingapp.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timetrackingapp.domain.model.UserProfile
import com.example.timetrackingapp.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    val currentUser: StateFlow<UserProfile?> = authRepository.currentUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _signInError = MutableStateFlow<String?>(null)
    val signInError = _signInError.asStateFlow()

    fun signInWithGoogle(idToken: String) {
        _signInError.value = null
        viewModelScope.launch {
            val result = authRepository.signInWithGoogle(idToken)
            if (result.isFailure) {
                // Captures the exact exception from Firebase
                _signInError.value = result.exceptionOrNull()?.localizedMessage ?: "Unknown Firebase error"
            }
        }
    }

    fun clearError() {
        _signInError.value = null
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }
    
    fun isUserSignedIn() = authRepository.isUserSignedIn()

    fun updateProfile(name: String) {
        viewModelScope.launch {
            authRepository.updateProfile(name)
        }
    }
}