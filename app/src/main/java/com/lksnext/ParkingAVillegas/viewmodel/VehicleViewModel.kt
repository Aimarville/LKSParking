package com.lksnext.ParkingAVillegas.viewmodel

import androidx.lifecycle.ViewModel
import com.lksnext.ParkingAVillegas.data.repository.UserRepository
import com.lksnext.ParkingAVillegas.model.Vehicle
import com.lksnext.ParkingAVillegas.ui.state.VehicleUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class VehicleViewModel(
    private val repository: UserRepository,
    private val userEmail: String
): ViewModel() {
    private val _uiState = MutableStateFlow(VehicleUiState())
    val uiState: StateFlow<VehicleUiState> = _uiState.asStateFlow()

    init {
        loadVehicles()
    }

    private fun loadVehicles() {
        val vehicles = repository
            .getUserByEmail(userEmail)
            ?.vehiculos
            ?:emptyList()

        _uiState.value = VehicleUiState(
            vehicles = vehicles
        )
    }

    fun addVehicle(vehicle: Vehicle) {
        val alreadyExists = repository
            .isPlateRegisteredGlobally(vehicle.plate)

        if (alreadyExists) {
            _uiState.value = _uiState.value.copy(
                error = "Este vehículo ya está registrado"
            )

            return
        }

        repository.addVehicleToUser(
            userEmail,
            vehicle
        )

        loadVehicles()
    }

    fun deleteVehicle(plate: String) {
        repository.removeVehicleFromUser(
            userEmail,
            plate
        )

        loadVehicles()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(
            error = null
        )
    }
}