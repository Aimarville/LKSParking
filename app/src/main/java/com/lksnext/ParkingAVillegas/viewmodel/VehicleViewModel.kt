package com.lksnext.ParkingAVillegas.viewmodel

import androidx.lifecycle.ViewModel
import com.lksnext.ParkingAVillegas.data.repository.vehicle.VehicleRepository
import com.lksnext.ParkingAVillegas.model.Vehicle
import com.lksnext.ParkingAVillegas.ui.state.VehicleUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class VehicleViewModel(
    private val vehicleRepository: VehicleRepository,
    private val userEmail: String
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(VehicleUiState())

    val uiState: StateFlow<VehicleUiState> =
        _uiState.asStateFlow()

    init {
        loadVehicles()
    }

    private fun loadVehicles() {

        val vehicles =
            vehicleRepository.getVehicles(userEmail)

        _uiState.value =
            _uiState.value.copy(
                vehicles = vehicles
            )
    }

    fun refresh() {
        loadVehicles()
    }

    fun addVehicle(vehicle: Vehicle) {

        val success =
            vehicleRepository.addVehicleToUser(
                userEmail,
                vehicle
            )

        if (!success) {

            _uiState.value =
                _uiState.value.copy(
                    error = "Este vehículo ya está registrado"
                )

            return
        }

        loadVehicles()
    }

    fun deleteVehicle(plate: String) {

        val success =
            vehicleRepository.removeVehicleFromUser(
                userEmail,
                plate
            )

        if (success) {
            loadVehicles()
        }
    }

    fun clearError() {
        _uiState.value =
            _uiState.value.copy(error = null)
    }
}