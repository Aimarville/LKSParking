package com.lksnext.ParkingAVillegas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lksnext.ParkingAVillegas.data.repository.auth.AuthRepository
import com.lksnext.ParkingAVillegas.model.User
import com.lksnext.ParkingAVillegas.ui.state.AuthUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun updateName(value: String) {
        _uiState.value = _uiState.value.copy(name = value)
    }

    fun updateEmail(value: String) {
        _uiState.value = _uiState.value.copy(email = value)
    }

    fun updatePhone(value: String) {
        _uiState.value = _uiState.value.copy(phone = value)
    }

    fun updateDepartment(value: String) {
        _uiState.value = _uiState.value.copy(department = value)
    }

    fun updatePassword(value: String) {
        _uiState.value = _uiState.value.copy(password = value)
    }

    fun updateConfirmPassword(value: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = value)
    }

    fun login(onSuccess: (User) -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            val result = repository.login(
                _uiState.value.email,
                _uiState.value.password
            )

            result
                .onSuccess { user ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        loggedUser = user,
                        isLogged = true
                    )

                    onSuccess(user)
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = it.message
                    )
                }
        }
    }

    fun register(
        onSuccess: (User) -> Unit
    ) {

        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    isLoading = true,
                    error = null
                )

            val result =
                repository.register(
                    name = _uiState.value.name,
                    email = _uiState.value.email,
                    phone = _uiState.value.phone,
                    department = _uiState.value.department,
                    password = _uiState.value.password,
                    confirmPassword = _uiState.value.confirmPassword
                )

            result
                .onSuccess { user ->

                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            registerSuccess = true,
                            loggedUser = user,
                            isLogged = true
                        )

                    onSuccess(user)
                }

                .onFailure {

                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            error = it.message
                        )
                }
        }
    }

    fun logout() {}

    fun forgotPassword() {}

    fun clearError() {
        _uiState.value = _uiState.value.copy(
            error = null
        )
    }

    fun clearRegisterSuccess() {
        _uiState.value = _uiState.value.copy(
            registerSuccess = false
        )
    }
}