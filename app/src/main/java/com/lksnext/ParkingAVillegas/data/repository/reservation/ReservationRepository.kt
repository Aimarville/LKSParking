package com.lksnext.ParkingAVillegas.data.repository.reservation

import com.lksnext.ParkingAVillegas.model.Reservation
import kotlinx.coroutines.flow.StateFlow

interface ReservationRepository {

    val reservations: StateFlow<List<Reservation>>

    fun getReservationsByUser(
        email: String
    ): List<Reservation>

    suspend fun addReservation(
        reservation: Reservation
    ): Boolean

    suspend fun updateReservation(
        reservation: Reservation
    ): Boolean

    suspend fun deleteReservation(
        reservationId: String
    )

    fun isSpotAvailable(
        spotId: String,
        dateMs: Long,
        startMs: Long,
        endMs: Long,
        excludeId: String? = null
    ): Boolean
}
