package com.lksnext.ParkingAVillegas.data.repository.vehicle

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.lksnext.ParkingAVillegas.data.repository.user.UserRepository
import com.lksnext.ParkingAVillegas.model.User
import com.lksnext.ParkingAVillegas.model.Vehicle
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class VehicleRepositoryImplTest {

    private lateinit var repository: VehicleRepositoryImpl
    private val userRepository: UserRepository = mockk()
    private val firestore: FirebaseFirestore = mockk()
    private val collectionReference: CollectionReference = mockk()
    private val documentReference: DocumentReference = mockk()
    
    private val taskQuerySnapshot: Task<QuerySnapshot> = mockk()
    private val querySnapshot: QuerySnapshot = mockk()
    private val documentSnapshot: DocumentSnapshot = mockk()
    private val taskVoid: Task<Void> = mockk()

    @Before
    fun setup() {
        mockkStatic("kotlinx.coroutines.tasks.TasksKt")
        repository = VehicleRepositoryImpl(userRepository, firestore)
        every { firestore.collection("users") } returns collectionReference
    }

    @After
    fun tearDown() {
        unmockkStatic("kotlinx.coroutines.tasks.TasksKt")
    }

    @Test
    fun `getVehicles returns user vehicles when user exists`() = runTest {
        val email = "test@lks.com"
        val vehicles = listOf(Vehicle(plate = "1234ABC"))
        coEvery { userRepository.getUserByEmail(email) } returns User(email = email, vehiculos = vehicles)
        assertEquals(vehicles, repository.getVehicles(email))
    }

    @Test
    fun `getVehicles returns empty list when user not found`() = runTest {
        coEvery { userRepository.getUserByEmail(any()) } returns null
        assertTrue(repository.getVehicles("none@lks.com").isEmpty())
    }

    @Test
    fun `addVehicleToUser returns true on success`() = runTest {
        val email = "test@lks.com"
        val user = User(uid = "uid123", email = email)
        
        // Mock global check (no duplicates)
        every { collectionReference.get() } returns taskQuerySnapshot
        coEvery { taskQuerySnapshot.await() } returns querySnapshot
        every { querySnapshot.documents } returns emptyList()

        coEvery { userRepository.getUserByEmail(email) } returns user
        every { collectionReference.document(user.uid) } returns documentReference
        every { documentReference.update("vehiculos", any()) } returns taskVoid
        coEvery { taskVoid.await() } returns mockk<Void>()

        assertTrue(repository.addVehicleToUser(email, Vehicle(plate = "NEW")))
    }

    @Test
    fun `addVehicleToUser returns false when plate already registered`() = runTest {
        val plate = "DUPE123"
        val userWithVehicle = User(vehiculos = listOf(Vehicle(plate = plate)))
        
        every { collectionReference.get() } returns taskQuerySnapshot
        coEvery { taskQuerySnapshot.await() } returns querySnapshot
        every { querySnapshot.documents } returns listOf(documentSnapshot)
        every { documentSnapshot.toObject(User::class.java) } returns userWithVehicle

        assertFalse(repository.addVehicleToUser("test@lks.com", Vehicle(plate = plate)))
    }

    @Test
    fun `addVehicleToUser returns false when user not found`() = runTest {
        every { collectionReference.get() } returns taskQuerySnapshot
        coEvery { taskQuerySnapshot.await() } returns querySnapshot
        every { querySnapshot.documents } returns emptyList()
        coEvery { userRepository.getUserByEmail(any()) } returns null

        assertFalse(repository.addVehicleToUser("none@lks.com", Vehicle(plate = "NEW")))
    }

    @Test
    fun `removeVehicleFromUser returns true on success`() = runTest {
        val email = "test@lks.com"
        val user = User(uid = "uid123", email = email, vehiculos = listOf(Vehicle(plate = "ABC")))

        coEvery { userRepository.getUserByEmail(email) } returns user
        every { collectionReference.document(user.uid) } returns documentReference
        every { documentReference.update("vehiculos", any()) } returns taskVoid
        coEvery { taskVoid.await() } returns mockk<Void>()

        assertTrue(repository.removeVehicleFromUser(email, "ABC"))
    }

    @Test
    fun `isPlateRegisteredGlobally returns true regardless of casing`() = runTest {
        val user = User(vehiculos = listOf(Vehicle(plate = "ABC-123")))
        every { collectionReference.get() } returns taskQuerySnapshot
        coEvery { taskQuerySnapshot.await() } returns querySnapshot
        every { querySnapshot.documents } returns listOf(documentSnapshot)
        every { documentSnapshot.toObject(User::class.java) } returns user

        assertTrue(repository.isPlateRegisteredGlobally("abc-123"))
    }
}
