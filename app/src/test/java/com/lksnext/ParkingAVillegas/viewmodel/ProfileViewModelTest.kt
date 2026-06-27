package com.lksnext.ParkingAVillegas.viewmodel

import android.net.Uri
import com.lksnext.ParkingAVillegas.data.repository.user.UserRepository
import com.lksnext.ParkingAVillegas.model.User
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
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
class ProfileViewModelTest {

    private lateinit var viewModel: ProfileViewModel
    private val userRepository: UserRepository = mockk()
    private val userEmail = "test@lks.com"
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockkStatic(Uri::class)
        
        coEvery { userRepository.getUserByEmail(userEmail) } returns User(
            nombre = "Test User",
            email = userEmail,
            telefono = "123",
            departamento = "IT"
        )
        
        viewModel = ProfileViewModel(userRepository, userEmail)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(Uri::class)
    }

    @Test
    fun `init loads user name correctly`() {
        assertEquals("Test User", viewModel.uiState.value.name)
    }

    @Test
    fun `init loads user email correctly`() {
        assertEquals(userEmail, viewModel.uiState.value.email)
    }

    @Test
    fun `updateName updates state`() {
        viewModel.updateName("New Name")
        assertEquals("New Name", viewModel.uiState.value.name)
    }

    @Test
    fun `toggleEditing enables editing`() {
        viewModel.toggleEditing()
        assertTrue(viewModel.uiState.value.isEditing)
    }

    @Test
    fun `saveProfile success sets success flag`() {
        coEvery { userRepository.updateProfile(any(), any(), any(), any(), any()) } returns true
        viewModel.saveProfile()
        assertTrue(viewModel.uiState.value.success)
    }

    @Test
    fun `saveProfile success disables editing`() {
        coEvery { userRepository.updateProfile(any(), any(), any(), any(), any()) } returns true
        viewModel.toggleEditing()
        viewModel.saveProfile()
        assertFalse(viewModel.uiState.value.isEditing)
    }

    @Test
    fun `saveProfile failure keeps editing enabled`() {
        coEvery { userRepository.updateProfile(any(), any(), any(), any(), any()) } returns false
        viewModel.toggleEditing()
        viewModel.saveProfile()
        assertTrue(viewModel.uiState.value.isEditing)
    }

    @Test
    fun `clearSuccess resets flag`() {
        coEvery { userRepository.updateProfile(any(), any(), any(), any(), any()) } returns true
        viewModel.saveProfile()
        viewModel.clearSuccess()
        assertFalse(viewModel.uiState.value.success)
    }

    @Test
    fun `refresh reloads user data`() {
        viewModel.refresh()
        coVerify(exactly = 2) { userRepository.getUserByEmail(userEmail) }
    }
}
