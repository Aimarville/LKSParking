package com.lksnext.ParkingAVillegas.data.repository.reservation

import androidx.compose.runtime.mutableStateListOf
import com.google.gson.reflect.TypeToken
import com.lksnext.ParkingAVillegas.data.local.JsonStorage
import com.lksnext.ParkingAVillegas.model.Reservation
import java.util.Calendar

class ReservationRepositoryImpl(
    private val storage: JsonStorage
): ReservationRepository {

    companion object {
        private const val FILE_NAME =
            "reservations.json"
    }

    private val _reservations =
        mutableStateListOf<Reservation>()

    override val reservations: List<Reservation>
        get() = _reservations

    init {
        load()
    }

    private fun load() {

        val type =
            object : TypeToken<List<Reservation>>() {}.type

        val loadedReservations: List<Reservation> =
            storage.read(
                FILE_NAME,
                type
            ) ?: emptyList()

        _reservations.clear()
        _reservations.addAll(loadedReservations)
    }

    private fun save() {

        storage.write(
            FILE_NAME,
            _reservations
        )
    }

    override fun getReservationsByUser(email: String): List<Reservation> {
        return reservations.filter {
            it.userEmail == email
        }
    }

    override fun addReservation(
        reservation: Reservation
    ): Boolean {

        val available =
            isSpotAvailable(
                reservation.spotId,
                reservation.date,
                reservation.startTime,
                reservation.endTime
            )

        if (!available) {
            return false
        }

        _reservations.add(reservation)

        save()

        return true
    }

    override fun updateReservation(
        reservation: Reservation
    ): Boolean {

        val available =
            isSpotAvailable(
                reservation.spotId,
                reservation.date,
                reservation.startTime,
                reservation.endTime,
                reservation.id
            )

        if (!available) {
            return false
        }

        val index =
            _reservations.indexOfFirst {
                it.id == reservation.id
            }

        if (index == -1) {
            return false
        }

        _reservations[index] = reservation

        save()

        return true
    }

    override fun deleteReservation(
        reservationId: String
    ) {

        _reservations.removeAll {
            it.id == reservationId
        }

        save()
    }

    override fun isSpotAvailable(
        spotId: String,
        dateMs: Long,
        startMs: Long,
        endMs: Long,
        excludeId: String?
    ): Boolean {

        return _reservations.none { reservation ->

            if (
                excludeId != null &&
                reservation.id == excludeId
            ) {
                return@none false
            }

            if (reservation.spotId != spotId) {
                return@none false
            }

            val cal1 =
                Calendar.getInstance().apply {
                    timeInMillis = reservation.date
                }

            val cal2 =
                Calendar.getInstance().apply {
                    timeInMillis = dateMs
                }

            val sameDay =
                cal1.get(Calendar.YEAR) ==
                        cal2.get(Calendar.YEAR) &&
                        cal1.get(Calendar.DAY_OF_YEAR) ==
                        cal2.get(Calendar.DAY_OF_YEAR)

            if (!sameDay) {
                return@none false
            }

            startMs < reservation.endTime &&
                    endMs > reservation.startTime
        }
    }
}