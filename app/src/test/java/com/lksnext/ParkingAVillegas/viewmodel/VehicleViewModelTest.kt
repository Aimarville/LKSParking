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
    fun `addVehicle success calls repository`() {
        val vehicle = Vehicle(plate = "123")
        coEvery { repository.addVehicleToUser(userEmail, vehicle) } returns true
        coEvery { repository.getVehicles(userEmail) } returns listOf(vehicle)

        viewModel.addVehicle(vehicle)
        coVerify { repository.addVehicleToUser(userEmail, vehicle) }
    }

    @Test
    fun `addVehicle success updates vehicle list`() {
        val vehicle = Vehicle(plate = "123")
        coEvery { repository.addVehicleToUser(userEmail, vehicle) } returns true
        coEvery { repository.getVehicles(userEmail) } returns listOf(vehicle)

        viewModel.addVehicle(vehicle)
        assertEquals(1, viewModel.uiState.value.vehicles.size)
    }

    @Test
    fun `addVehicle failure sets error message`() {
        coEvery { repository.addVehicleToUser(userEmail, any()) } returns false
        viewModel.addVehicle(Vehicle(plate = "123"))
        assertEquals("Este vehículo ya está registrado", viewModel.uiState.value.error)
    }

    @Test
    fun `deleteVehicle success calls repository`() {
        val plate = "123"
        coEvery { repository.removeVehicleFromUser(userEmail, plate) } returns true
        viewModel.deleteVehicle(plate)
        coVerify { repository.removeVehicleFromUser(userEmail, plate) }
    }

    @Test
    fun `deleteVehicle success reloads list`() {
        coEvery { repository.removeVehicleFromUser(any(), any()) } returns true
        viewModel.deleteVehicle("123")
        coVerify(exactly = 2) { repository.getVehicles(userEmail) }
    }

    @Test
    fun `clearError removes error state`() {
        coEvery { repository.addVehicleToUser(any(), any()) } returns false
        viewModel.addVehicle(Vehicle())
        viewModel.clearError()
        assertNull(viewModel.uiState.value.error)
    }
}
