package com.lksnext.ParkingAVillegas.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.lksnext.ParkingAVillegas.data.repository.UserRepository
import com.lksnext.ParkingAVillegas.ui.state.ProfileUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProfileViewModel(
    private val repository: UserRepository,
    private val userEmail: String
): ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUser()
    }

    private fun loadUser() {
        val user = repository.getUserByEmail(userEmail)

        if (user != null) {
            _uiState.value = ProfileUiState(
                name = user.nombre,
                email = user.email,
                phone = user.telefono,
                department = user.departamento,
                photoUri = user.profilePhotoUri?.let {Uri.parse(it)},
                vehicleCount = user.vehiculos.size
            )
        }
    }

    fun updateName(value: String) {
        _uiState.value = _uiState.value.copy(name = value)
    }

    fun updatePhone(value: String) {
        _uiState.value = _uiState.value.copy(phone = value)
    }

    fun updateDepartment(value: String) {
        _uiState.value = _uiState.value.copy(department = value)
    }

    fun updatePhoto(uri: Uri?) {
        _uiState.value = _uiState.value.copy(photoUri = uri)
    }

    fun toggleEditing() {
        _uiState.value = _uiState.value.copy(
            isEditing = !_uiState.value.isEditing
        )
    }

    fun saveProfile() {
        repository.updateProfile(
            email = userEmail,
            newName = _uiState.value.name,
            newPhone = _uiState.value.phone,
            newDept = _uiState.value.department,
            photoUri = _uiState.value.photoUri?.toString()
        )

        _uiState.value = _uiState.value.copy(
            success = true,
            isEditing = false
        )
    }

    fun clearSuccess() {
        _uiState.value = _uiState.value.copy(
            success = false
        )
    }
}