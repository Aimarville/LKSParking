package com.lksnext.ParkingAVillegas.data.repository.reservation

import com.google.firebase.firestore.FirebaseFirestore
import com.lksnext.ParkingAVillegas.model.Reservation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import java.util.Calendar

class ReservationRepositoryImpl : ReservationRepository {

    companion object {
        private const val COLLECTION = "reservations"
    }

    private val firestore = FirebaseFirestore.getInstance()

    private val _reservations = MutableStateFlow<List<Reservation>>(emptyList())
    override val reservations: StateFlow<List<Reservation>> = _reservations.asStateFlow()

    init {
        firestore.collection(COLLECTION)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                _reservations.value = snapshot.documents.mapNotNull {
                    it.toObject(Reservation::class.java)
                }
            }
    }

    override fun getReservationsByUser(email: String): List<Reservation> {
        return _reservations.value.filter { it.userEmail == email }
    }

    override suspend fun addReservation(reservation: Reservation): Boolean {

        if (!isSpotAvailable(
                reservation.spotId,
                reservation.date,
                reservation.startTime,
                reservation.endTime
            )
        ) return false

        return try {
            firestore.collection(COLLECTION)
                .document(reservation.id)
                .set(reservation)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun updateReservation(reservation: Reservation): Boolean {

        if (!isSpotAvailable(
                reservation.spotId,
                reservation.date,
                reservation.startTime,
                reservation.endTime,
                reservation.id
            )
        ) return false

        return try {
            firestore.collection(COLLECTION)
                .document(reservation.id)
                .set(reservation)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun deleteReservation(reservationId: String) {
        try {
            firestore.collection(COLLECTION)
                .document(reservationId)
                .delete()
                .await()
        } catch (_: Exception) {}
    }

    override fun isSpotAvailable(
        spotId: String,
        dateMs: Long,
        startMs: Long,
        endMs: Long,
        excludeId: String?
    ): Boolean {

        return _reservations.value.none { reservation ->

            if (excludeId != null && reservation.id == excludeId) return@none false

            if (reservation.spotId != spotId) return@none false

            val cal1 = Calendar.getInstance().apply { timeInMillis = reservation.date }
            val cal2 = Calendar.getInstance().apply { timeInMillis = dateMs }

            val sameDay =
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                        cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)

            if (!sameDay) return@none false

            startMs < reservation.endTime && endMs > reservation.startTime
        }
    }
}
