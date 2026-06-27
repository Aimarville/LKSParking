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
        
        // Default mock for initial refresh() call in init
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
    fun `init loads user data correctly`() {
        val state = viewModel.uiState.value
        assertEquals("Test User", state.name)
        assertEquals(userEmail, state.email)
        assertEquals("IT", state.department)
    }

    @Test
    fun `update methods update state`() {
        viewModel.updateName("New Name")
        viewModel.updatePhone("999")
        viewModel.updateDepartment("HR")
        
        val state = viewModel.uiState.value
        assertEquals("New Name", state.name)
        assertEquals("999", state.phone)
        assertEquals("HR", state.department)
    }

    @Test
    fun `toggleEditing changes isEditing state`() {
        assertFalse(viewModel.uiState.value.isEditing)
        viewModel.toggleEditing()
        assertTrue(viewModel.uiState.value.isEditing)
        viewModel.toggleEditing()
        assertFalse(viewModel.uiState.value.isEditing)
    }

    @Test
    fun `saveProfile success updates state and reloads user`() {
        coEvery { 
            userRepository.updateProfile(userEmail, any(), any(), any(), any()) 
        } returns true
        
        viewModel.toggleEditing()
        viewModel.updateName("Updated Name")
        viewModel.saveProfile()
        
        coVerify { 
            userRepository.updateProfile(userEmail, "Updated Name", any(), any(), any()) 
        }
        
        val state = viewModel.uiState.value
        assertTrue(state.success)
        assertFalse(state.isEditing)
    }

    @Test
    fun `saveProfile failure keeps editing state and sets no success`() {
        coEvery { 
            userRepository.updateProfile(any(), any(), any(), any(), any()) 
        } returns false
        
        viewModel.toggleEditing()
        viewModel.saveProfile()
        
        val state = viewModel.uiState.value
        assertFalse(state.success)
        assertTrue(state.isEditing)
    }

    @Test
    fun `clearSuccess sets success to false`() {
        coEvery { userRepository.updateProfile(any(), any(), any(), any(), any()) } returns true
        viewModel.saveProfile()
        assertTrue(viewModel.uiState.value.success)
        
        viewModel.clearSuccess()
        assertFalse(viewModel.uiState.value.success)
    }

    @Test
    fun `updatePhoto updates state`() {
        val mockUri = mockk<Uri>()
        viewModel.updatePhoto(mockUri)
        assertEquals(mockUri, viewModel.uiState.value.photoUri)
    }

    @Test
    fun `refresh reloads user data`() {
        viewModel.refresh()
        coVerify(exactly = 2) { userRepository.getUserByEmail(userEmail) }
    }
}
