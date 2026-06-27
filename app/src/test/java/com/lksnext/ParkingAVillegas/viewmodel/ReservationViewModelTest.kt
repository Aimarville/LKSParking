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
import kotlinx.coroutines.test.runTest
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

    private fun setupValidReservationState() {
        viewModel.selectVehicle(Vehicle(plate = "123", type = VehicleType.AUTOMOBILE))
        viewModel.selectDate(Calendar.getInstance())
        viewModel.selectStartTime(Calendar.getInstance())
        viewModel.selectEndTime(Calendar.getInstance())
        viewModel.selectSpot(ParkingSpot("P1", SpotType.NORMAL))
    }

    @Test
    fun `init loads user correctly`() {
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
        val vehicle = Vehicle(plate = "123")
        viewModel.selectVehicle(vehicle)
        assertEquals(vehicle, viewModel.uiState.value.selectedVehicle)
    }

    @Test
    fun `selectDate updates state`() {
        val now = Calendar.getInstance()
        viewModel.selectDate(now)
        assertEquals(now, viewModel.uiState.value.selectedDate)
    }

    @Test
    fun `selectStartTime updates state`() {
        val now = Calendar.getInstance()
        viewModel.selectStartTime(now)
        assertEquals(now, viewModel.uiState.value.startTime)
    }

    @Test
    fun `selectEndTime updates state`() {
        val now = Calendar.getInstance()
        viewModel.selectVehicle(Vehicle(plate = "123")) 
        viewModel.selectEndTime(now)
        assertEquals(now, viewModel.uiState.value.endTime)
    }

    @Test
    fun `selectSpot updates state`() {
        val spot = ParkingSpot("1", SpotType.NORMAL)
        viewModel.selectSpot(spot)
        assertEquals(spot, viewModel.uiState.value.selectedSpot)
    }

    @Test
    fun `updateFilter updates filterType state`() {
        viewModel.updateFilter("ELECTRIC")
        assertEquals("ELECTRIC", viewModel.uiState.value.filterType)
    }

    @Test
    fun `createReservation success sets success flag`() = runTest {
        setupValidReservationState()
        coEvery { reservationRepository.addReservation(any()) } returns true
        
        viewModel.createReservation()
        assertTrue(viewModel.uiState.value.success)
    }

    @Test
    fun `createReservation success resets current step`() = runTest {
        setupValidReservationState()
        coEvery { reservationRepository.addReservation(any()) } returns true
        
        viewModel.createReservation()
        assertEquals(1, viewModel.uiState.value.currentStep)
    }

    @Test
    fun `createReservation failure sets error message`() = runTest {
        setupValidReservationState()
        coEvery { reservationRepository.addReservation(any()) } returns false
        
        viewModel.createReservation()
        assertEquals("La plaza ya no está disponible", viewModel.uiState.value.error)
    }

    @Test
    fun `deleteReservation calls repository`() {
        coEvery { reservationRepository.deleteReservation(any()) } returns Unit
        viewModel.deleteReservation("1")
        coVerify { reservationRepository.deleteReservation("1") }
    }

    @Test
    fun `updateReservation success sets updateSuccess`() = runTest {
        val res = Reservation(id = "1", userEmail = userEmail, vehiclePlate = "123", spotId = "P1", date = 0, startTime = 0, endTime = 0)
        coEvery { reservationRepository.updateReservation(any()) } returns true
        
        viewModel.updateReservation(res)
        assertTrue(viewModel.uiState.value.updateSuccess)
    }

    @Test
    fun `updateReservation failure sets error`() = runTest {
        val res = Reservation(id = "1", userEmail = userEmail, vehiclePlate = "123", spotId = "P1", date = 0, startTime = 0, endTime = 0)
        coEvery { reservationRepository.updateReservation(any()) } returns false
        
        viewModel.updateReservation(res)
        assertEquals("La plaza ya no está disponible", viewModel.uiState.value.error)
    }

    @Test
    fun `clearSuccess resets success state`() = runTest {
        setupValidReservationState()
        coEvery { reservationRepository.addReservation(any()) } returns true
        viewModel.createReservation()
        
        viewModel.clearSuccess()
        assertFalse(viewModel.uiState.value.success)
    }

    @Test
    fun `clearUpdateSuccess resets updateSuccess state`() {
        coEvery { reservationRepository.updateReservation(any()) } returns true
        viewModel.updateReservation(Reservation())
        
        viewModel.clearUpdateSuccess()
        assertFalse(viewModel.uiState.value.updateSuccess)
    }

    @Test
    fun `clearError resets error state`() = runTest {
        setupValidReservationState()
        coEvery { reservationRepository.addReservation(any()) } returns false
        viewModel.createReservation()

        viewModel.clearError()
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `resetForm resets current step`() {
        viewModel.updateStep(3)
        viewModel.resetForm()
        assertEquals(1, viewModel.uiState.value.currentStep)
    }
}
