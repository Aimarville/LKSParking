package com.lksnext.ParkingAVillegas.viewmodel

import com.lksnext.ParkingAVillegas.data.repository.reservation.ReservationRepository
import com.lksnext.ParkingAVillegas.data.repository.user.UserRepository
import com.lksnext.ParkingAVillegas.model.User
import com.lksnext.ParkingAVillegas.model.Vehicle
import com.lksnext.ParkingAVillegas.model.VehicleType
import com.lksnext.ParkingAVillegas.model.ParkingSpot
import com.lksnext.ParkingAVillegas.model.SpotType
import com.lksnext.ParkingAVillegas.model.Reservation
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.Calendar

@OptIn(ExperimentalCoroutinesApi::class)
class ReservationViewModelTest {

    private lateinit var viewModel: ReservationViewModel
    private val reservationRepository: ReservationRepository = mockk()
    private val userRepository: UserRepository = mockk()
    private val userEmail = "test@lks.com"
    private val testDispatcher = UnconfinedTestDispatcher()
    private val reservationsFlow = MutableStateFlow(emptyList<Reservation>())

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        // Mock necessary calls during init
        every { reservationRepository.reservations } returns reservationsFlow
        coEvery { userRepository.getUserByEmail(userEmail) } returns User(email = userEmail)
        every { reservationRepository.getReservationsByUser(userEmail) } returns emptyList()
        
        // Mock spot availability as it is called automatically when setting end time
        every { 
            reservationRepository.isSpotAvailable(any(), any(), any(), any(), any()) 
        } returns true
        
        viewModel = ReservationViewModel(reservationRepository, userRepository, userEmail)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads user and reservations`() {
        coVerify { userRepository.getUserByEmail(userEmail) }
        assertNotNull(viewModel.uiState.value.user)
    }

    @Test
    fun `refresh reloads data`() {
        viewModel.refresh()
        coVerify(exactly = 2) { userRepository.getUserByEmail(userEmail) }
    }

    @Test
    fun `updateStep updates currentStep`() {
        viewModel.updateStep(2)
        assertEquals(2, viewModel.uiState.value.currentStep)
    }

    @Test
    fun `selectVehicle updates state`() {
        val vehicle = Vehicle(plate = "123", type = VehicleType.AUTOMOBILE)
        viewModel.selectVehicle(vehicle)
        assertEquals(vehicle, viewModel.uiState.value.selectedVehicle)
    }

    @Test
    fun `selection methods update state`() {
        val now = Calendar.getInstance()
        viewModel.selectDate(now)
        viewModel.selectStartTime(now)
        // Set vehicle to avoid early exit in loadAvailableSpots
        viewModel.selectVehicle(Vehicle(plate = "123"))
        viewModel.selectEndTime(now)
        val spot = ParkingSpot("1", SpotType.NORMAL)
        viewModel.selectSpot(spot)
        
        assertEquals(now, viewModel.uiState.value.selectedDate)
        assertEquals(now, viewModel.uiState.value.startTime)
        assertEquals(now, viewModel.uiState.value.endTime)
        assertEquals(spot, viewModel.uiState.value.selectedSpot)
    }

    @Test
    fun `updateFilter updates state and reloads spots`() {
        viewModel.selectVehicle(Vehicle(plate = "123"))
        viewModel.selectDate(Calendar.getInstance())
        viewModel.selectStartTime(Calendar.getInstance())
        viewModel.selectEndTime(Calendar.getInstance())
        
        viewModel.updateFilter("ELECTRIC")
        assertEquals("ELECTRIC", viewModel.uiState.value.filterType)
        coVerify { reservationRepository.isSpotAvailable(any(), any(), any(), any(), any()) }
    }

    @Test
    fun `createReservation success resets form and sets success true`() {
        val vehicle = Vehicle(plate = "123", type = VehicleType.AUTOMOBILE)
        val spot = ParkingSpot("P1", SpotType.NORMAL)
        val now = Calendar.getInstance()
        
        viewModel.selectVehicle(vehicle)
        viewModel.selectDate(now)
        viewModel.selectStartTime(now)
        viewModel.selectEndTime(now)
        viewModel.selectSpot(spot)
        
        coEvery { reservationRepository.addReservation(any()) } returns true
        
        viewModel.createReservation()
        
        val state = viewModel.uiState.value
        assertTrue(state.success)
        assertNull(state.selectedVehicle)
        assertEquals(1, state.currentStep)
    }

    @Test
    fun `createReservation failure sets error`() {
        val vehicle = Vehicle(plate = "123", type = VehicleType.AUTOMOBILE)
        val spot = ParkingSpot("P1", SpotType.NORMAL)
        val now = Calendar.getInstance()
        
        viewModel.selectVehicle(vehicle)
        viewModel.selectDate(now)
        viewModel.selectStartTime(now)
        viewModel.selectEndTime(now)
        viewModel.selectSpot(spot)
        
        coEvery { reservationRepository.addReservation(any()) } returns false
        
        viewModel.createReservation()
        
        val state = viewModel.uiState.value
        assertFalse(state.success)
        assertEquals("La plaza ya no está disponible", state.error)
    }

    @Test
    fun `deleteReservation calls repository`() {
        coEvery { reservationRepository.deleteReservation(any()) } returns Unit
        viewModel.deleteReservation("1")
        coVerify { reservationRepository.deleteReservation("1") }
    }

    @Test
    fun `updateReservation success sets updateSuccess`() {
        val res = Reservation(id = "1", userEmail = userEmail, vehiclePlate = "123", spotId = "P1", date = 0, startTime = 0, endTime = 0)
        coEvery { reservationRepository.updateReservation(any()) } returns true
        
        viewModel.updateReservation(res)
        assertTrue(viewModel.uiState.value.updateSuccess)
    }

    @Test
    fun `updateReservation failure sets error`() {
        val res = Reservation(id = "1", userEmail = userEmail, vehiclePlate = "123", spotId = "P1", date = 0, startTime = 0, endTime = 0)
        coEvery { reservationRepository.updateReservation(any()) } returns false
        
        viewModel.updateReservation(res)
        assertEquals("La plaza ya no está disponible", viewModel.uiState.value.error)
    }

    @Test
    fun `clear methods reset success and error states`() {
        val vehicle = Vehicle(plate = "123", type = VehicleType.AUTOMOBILE)
        val spot = ParkingSpot("P1", SpotType.NORMAL)
        val now = Calendar.getInstance()
        
        viewModel.selectVehicle(vehicle)
        viewModel.selectDate(now)
        viewModel.selectStartTime(now)
        viewModel.selectEndTime(now)
        viewModel.selectSpot(spot)
        
        coEvery { reservationRepository.addReservation(any()) } returns true
        viewModel.createReservation()
        assertTrue(viewModel.uiState.value.success)
        
        viewModel.clearSuccess()
        assertFalse(viewModel.uiState.value.success)
        
        coEvery { reservationRepository.updateReservation(any()) } returns true
        viewModel.updateReservation(Reservation())
        assertTrue(viewModel.uiState.value.updateSuccess)
        
        viewModel.clearUpdateSuccess()
        assertFalse(viewModel.uiState.value.updateSuccess)
        
        coEvery { reservationRepository.addReservation(any()) } returns false
        // Re-select to fill required fields for createReservation
        viewModel.selectVehicle(vehicle)
        viewModel.selectDate(now)
        viewModel.selectStartTime(now)
        viewModel.selectEndTime(now)
        viewModel.selectSpot(spot)

        viewModel.createReservation()
        assertNotNull(viewModel.uiState.value.error)
        
        viewModel.clearError()
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `resetForm clears all fields`() {
        viewModel.updateStep(3)
        viewModel.resetForm()
        
        val state = viewModel.uiState.value
        assertEquals(1, state.currentStep)
        assertNull(state.selectedVehicle)
        assertFalse(state.success)
    }
}
