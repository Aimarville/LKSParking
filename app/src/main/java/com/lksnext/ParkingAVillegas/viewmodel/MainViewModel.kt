package com.lksnext.ParkingAVillegas.viewmodel

import androidx.lifecycle.ViewModel
import com.lksnext.ParkingAVillegas.model.User
import com.lksnext.ParkingAVillegas.ui.state.MainUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel(
    user: User?
): ViewModel() {
    private val _uiState =
        MutableStateFlow(
            MainUiState(
                currentUser = user
            )
        )

    val uiState: StateFlow<MainUiState> =
        _uiState.asStateFlow()

    fun navigate(route: String) {
        _uiState.value = _uiState.value.copy(
            currentRoute = route
        )
    }

    fun showNotifications() {
        _uiState.value =
            _uiState.value.copy(
                showNotifications = true
            )
    }

    fun hideNotifications() {
        _uiState.value =
            _uiState.value.copy(
                showNotifications = false
            )
    }

    fun markAllNotificationsAsRead() {
        _uiState.value =
            _uiState.value.copy(
                notificationCount = 0
            )
    }
}