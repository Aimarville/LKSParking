package com.lksnext.ParkingAVillegas.data.repository.reservation

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import com.lksnext.ParkingAVillegas.model.Reservation
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.slot
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.Calendar

class ReservationRepositoryImplTest {

    private lateinit var repository: ReservationRepositoryImpl
    private val firestore: FirebaseFirestore = mockk()
    private val collectionReference: CollectionReference = mockk()
    private val documentReference: DocumentReference = mockk()
    private val listenerRegistration: ListenerRegistration = mockk()
    private val taskVoid: Task<Void> = mockk()
    private val snapshot: QuerySnapshot = mockk()

    @Before
    fun setup() {
        mockkStatic("kotlinx.coroutines.tasks.TasksKt")
        every { firestore.collection("reservations") } returns collectionReference
        every { collectionReference.addSnapshotListener(any<EventListener<QuerySnapshot>>()) } returns listenerRegistration
        
        repository = ReservationRepositoryImpl(firestore)
    }

    @After
    fun tearDown() {
        unmockkStatic("kotlinx.coroutines.tasks.TasksKt")
    }

    private fun setupMockReservations(list: List<Reservation>) {
        val listenerSlot = slot<EventListener<QuerySnapshot>>()
        every { collectionReference.addSnapshotListener(capture(listenerSlot)) } returns listenerRegistration
        repository = ReservationRepositoryImpl(firestore)

        every { snapshot.documents } returns list.map { res ->
            mockk { every { toObject(Reservation::class.java) } returns res }
        }
        listenerSlot.captured.onEvent(snapshot, null)
    }

    @Test
    fun `getReservationsByUser returns correct number of results`() {
        setupMockReservations(listOf(
            Reservation(id = "1", userEmail = "user1@lks.com"),
            Reservation(id = "2", userEmail = "user2@lks.com")
        ))
        val results = repository.getReservationsByUser("user1@lks.com")
        assertEquals(1, results.size)
    }

    @Test
    fun `getReservationsByUser returns expected reservation ID`() {
        setupMockReservations(listOf(
            Reservation(id = "1", userEmail = "user1@lks.com")
        ))
        val results = repository.getReservationsByUser("user1@lks.com")
        assertEquals("1", results[0].id)
    }

    @Test
    fun `addReservation returns true when spot is available`() = runTest {
        val reservation = Reservation(id = "res1", spotId = "A-01")
        every { collectionReference.document(reservation.id) } returns documentReference
        every { documentReference.set(reservation) } returns taskVoid
        coEvery { taskVoid.await() } returns mockk<Void>()

        assertTrue(repository.addReservation(reservation))
    }

    @Test
    fun `updateReservation returns false when spot is taken by another`() = runTest {
        setupMockReservations(listOf(
            Reservation(id = "other", spotId = "A-01", date = 1000, startTime = 1000, endTime = 2000)
        ))
        val updated = Reservation(id = "my-res", spotId = "A-01", date = 1000, startTime = 1500, endTime = 2500)
        assertFalse(repository.updateReservation(updated))
    }

    @Test
    fun `isSpotAvailable returns false on time overlap`() {
        val date = Calendar.getInstance().apply { set(2023, 10, 20) }.timeInMillis
        setupMockReservations(listOf(
            Reservation(id = "1", spotId = "A-01", date = date, startTime = 1000, endTime = 2000)
        ))
        assertFalse(repository.isSpotAvailable("A-01", date, 1500, 2500))
    }

    @Test
    fun `isSpotAvailable returns true when times are adjacent`() {
        val date = Calendar.getInstance().apply { set(2023, 10, 20) }.timeInMillis
        setupMockReservations(listOf(
            Reservation(id = "1", spotId = "A-01", date = date, startTime = 1000, endTime = 2000)
        ))
        assertTrue(repository.isSpotAvailable("A-01", date, 2000, 3000))
    }
}
