package com.lksnext.ParkingAVillegas.viewmodel

import androidx.lifecycle.ViewModel
import com.lksnext.ParkingAVillegas.data.repository.reservation.ReservationRepository
import com.lksnext.ParkingAVillegas.data.repository.user.UserRepository
import com.lksnext.ParkingAVillegas.data.static.ParkingData
import com.lksnext.ParkingAVillegas.model.*
import com.lksnext.ParkingAVillegas.ui.state.ReservationUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar
import java.util.UUID

class ReservationViewModel(
    private val reservationRepository: ReservationRepository,
    private val userRepository: UserRepository,
    private val userEmail: String
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(
            ReservationUiState(user = getCurrentUser())
        )

    val uiState: StateFlow<ReservationUiState> =
        _uiState.asStateFlow()

    init {
        refresh()
    }

    private fun getCurrentUser(): User? {
        return userRepository.getUserByEmail(userEmail)
    }

    fun refresh() {
        _uiState.value =
            _uiState.value.copy(
                user = getCurrentUser()
            )

        loadReservations()
    }

    private fun loadReservations() {

        val reservations =
            reservationRepository
                .getReservationsByUser(userEmail)

        val now = System.currentTimeMillis()

        _uiState.value =
            _uiState.value.copy(
                user = getCurrentUser(),

                currentReservations =
                    reservations
                        .filter { it.endTime >= now }
                        .sortedBy { it.startTime },

                historicalReservations =
                    reservations
                        .filter { it.endTime < now }
                        .sortedByDescending { it.startTime }
            )
    }

    fun updateStep(step: Int) {
        _uiState.value =
            _uiState.value.copy(currentStep = step)
    }

    fun selectVehicle(vehicle: Vehicle) {
        _uiState.value =
            _uiState.value.copy(selectedVehicle = vehicle)
    }

    fun selectDate(date: Calendar) {
        _uiState.value =
            _uiState.value.copy(selectedDate = date)
    }

    fun selectStartTime(time: Calendar) {
        _uiState.value =
            _uiState.value.copy(startTime = time)
    }

    fun selectEndTime(time: Calendar) {
        _uiState.value =
            _uiState.value.copy(endTime = time)

        loadAvailableSpots()
    }

    fun selectSpot(spot: ParkingSpot) {
        _uiState.value =
            _uiState.value.copy(selectedSpot = spot)
    }

    fun updateFilter(filter: String) {

        _uiState.value =
            _uiState.value.copy(filterType = filter)

        loadAvailableSpots()
    }

    private fun loadAvailableSpots() {

        val vehicle = _uiState.value.selectedVehicle
        val date = _uiState.value.selectedDate
        val start = _uiState.value.startTime
        val end = _uiState.value.endTime

        if (
            vehicle == null ||
            date == null ||
            start == null ||
            end == null
        ) return

        val spots =
            ParkingData.allSpots.filter { spot ->

                val compatible =
                    when (vehicle.type) {

                        VehicleType.MOTORCYCLE -> {
                            spot.type == SpotType.MOTORCYCLE
                        }

                        else -> {
                            when (spot.type) {

                                SpotType.NORMAL -> true

                                SpotType.ELECTRIC ->
                                    vehicle.isElectric

                                SpotType.DISABLED ->
                                    vehicle.isDisabled

                                else -> false
                            }
                        }
                    }

                val available =
                    reservationRepository.isSpotAvailable(
                        spotId = spot.id,
                        dateMs = date.timeInMillis,
                        startMs = start.timeInMillis,
                        endMs = end.timeInMillis
                    )

                val matchesFilter =
                    _uiState.value.filterType == "TODAS" ||
                            spot.type.name == _uiState.value.filterType

                compatible &&
                        available &&
                        matchesFilter
            }

        _uiState.value =
            _uiState.value.copy(
                availableSpots = spots
            )
    }

    fun createReservation(): Boolean {

        val state = _uiState.value

        val reservation =
            Reservation(
                id = UUID.randomUUID().toString(),
                userEmail = userEmail,
                vehiclePlate = state.selectedVehicle!!.plate,
                spotId = state.selectedSpot!!.id,
                date = state.selectedDate!!.timeInMillis,
                startTime = state.startTime!!.timeInMillis,
                endTime = state.endTime!!.timeInMillis
            )

        val success =
            reservationRepository.addReservation(reservation)

        if (success) {

            _uiState.value =
                _uiState.value.copy(
                    success = true,
                    currentStep = 1,
                    selectedVehicle = null,
                    selectedDate = null,
                    startTime = null,
                    endTime = null,
                    selectedSpot = null,
                    availableSpots = emptyList(),
                    filterType = "TODAS"
                )

            refresh()
        }

        return success
    }

    fun resetForm() {
        _uiState.value =
            _uiState.value.copy(
                currentStep = 1,
                selectedVehicle = null,
                selectedDate = null,
                startTime = null,
                endTime = null,
                selectedSpot = null,
                availableSpots = emptyList(),
                filterType = "TODAS",
                success = false,
                error = null
            )
    }

    fun deleteReservation(id: String) {

        reservationRepository.deleteReservation(id)

        refresh()
    }

    fun updateReservation(
        reservation: Reservation
    ): Boolean {

        val success =
            reservationRepository.updateReservation(reservation)

        if (success) {
            refresh()
        }

        return success
    }

    fun clearSuccess() {
        _uiState.value =
            _uiState.value.copy(success = false)
    }

    fun clearError() {
        _uiState.value =
            _uiState.value.copy(error = null)
    }
}
