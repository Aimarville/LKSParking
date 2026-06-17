package com.lksnext.ParkingAVillegas.data.repository.reservation

import com.lksnext.ParkingAVillegas.model.Reservation

interface ReservationRepository {

    val reservations: List<Reservation>

    fun getReservationsByUser(
        email: String
    ): List<Reservation>

    fun addReservation(
        reservation: Reservation
    ): Boolean

    fun updateReservation(
        reservation: Reservation
    ): Boolean

    fun deleteReservation(
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