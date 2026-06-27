package com.lksnext.ParkingAVillegas.viewmodel

import com.lksnext.ParkingAVillegas.data.repository.vehicle.VehicleRepository
import com.lksnext.ParkingAVillegas.model.Vehicle
import com.lksnext.ParkingAVillegas.model.VehicleType
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class VehicleViewModelTest {

    private lateinit var viewModel: VehicleViewModel
    private val repository: VehicleRepository = mockk()
    private val userEmail = "test@lks.com"
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        // Mock initial loadVehicles call in init
        coEvery { repository.getVehicles(userEmail) } returns emptyList()
        viewModel = VehicleViewModel(repository, userEmail)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init calls loadVehicles`() {
        coVerify { repository.getVehicles(userEmail) }
    }

    @Test
    fun `addVehicle success reloads vehicles`() {
        val vehicle = Vehicle(plate = "1234ABC", brand = "Toyota", model = "Corolla", type = VehicleType.AUTOMOBILE)
        coEvery { repository.addVehicleToUser(userEmail, vehicle) } returns true
        coEvery { repository.getVehicles(userEmail) } returns listOf(vehicle)

        viewModel.addVehicle(vehicle)

        coVerify { repository.addVehicleToUser(userEmail, vehicle) }
        assertEquals(1, viewModel.uiState.value.vehicles.size)
        assertEquals(vehicle, viewModel.uiState.value.vehicles[0])
    }

    @Test
    fun `addVehicle failure sets error`() {
        val vehicle = Vehicle(plate = "1234ABC", brand = "Toyota", model = "Corolla", type = VehicleType.AUTOMOBILE)
        coEvery { repository.addVehicleToUser(userEmail, vehicle) } returns false

        viewModel.addVehicle(vehicle)

        assertEquals("Este vehículo ya está registrado", viewModel.uiState.value.error)
    }

    @Test
    fun `deleteVehicle success reloads vehicles`() {
        val plate = "1234ABC"
        coEvery { repository.removeVehicleFromUser(userEmail, plate) } returns true
        coEvery { repository.getVehicles(userEmail) } returns emptyList()

        viewModel.deleteVehicle(plate)

        coVerify { repository.removeVehicleFromUser(userEmail, plate) }
        assertTrue(viewModel.uiState.value.vehicles.isEmpty())
    }

    @Test
    fun `clearError removes error from state`() {
        coEvery { repository.addVehicleToUser(any(), any()) } returns false
        viewModel.addVehicle(Vehicle())
        
        assertNotNull(viewModel.uiState.value.error)
        
        viewModel.clearError()
        assertNull(viewModel.uiState.value.error)
    }
}
