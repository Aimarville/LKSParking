package com.lksnext.ParkingAVillegas.viewmodel

import com.lksnext.ParkingAVillegas.data.repository.auth.AuthRepository
import com.lksnext.ParkingAVillegas.model.User
import io.mockk.coEvery
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
class AuthViewModelTest {

    private lateinit var viewModel: AuthViewModel
    private val repository: AuthRepository = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = AuthViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is empty`() {
        val state = viewModel.uiState.value
        assertEquals("", state.name)
        assertEquals("", state.email)
        assertEquals("", state.password)
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `update methods update the state`() {
        viewModel.updateName("Name")
        viewModel.updateEmail("test@lks.com")
        viewModel.updatePhone("123")
        viewModel.updateDepartment("IT")
        viewModel.updatePassword("pass")
        viewModel.updateConfirmPassword("pass")

        val state = viewModel.uiState.value
        assertEquals("Name", state.name)
        assertEquals("test@lks.com", state.email)
        assertEquals("123", state.phone)
        assertEquals("IT", state.department)
        assertEquals("pass", state.password)
        assertEquals("pass", state.confirmPassword)
    }

    @Test
    fun `login success updates state and triggers callback`() {
        val user = User(uid = "123", email = "test@lks.com")
        coEvery { repository.login(any(), any()) } returns Result.success(user)
        
        var callbackCalled = false
        viewModel.updateEmail("test@lks.com")
        viewModel.updatePassword("password")
        
        viewModel.login {
            callbackCalled = true
        }

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.isLogged)
        assertEquals(user, state.loggedUser)
        assertTrue(callbackCalled)
    }

    @Test
    fun `login failure updates error state`() {
        val errorMessage = "Invalid credentials"
        coEvery { repository.login(any(), any()) } returns Result.failure(Exception(errorMessage))
        
        viewModel.login { }

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertFalse(state.isLogged)
        assertEquals(errorMessage, state.error)
    }

    @Test
    fun `register success updates state`() {
        val user = User(uid = "123", email = "test@lks.com")
        coEvery { repository.register(any(), any(), any(), any(), any(), any()) } returns Result.success(user)
        
        viewModel.register { }

        val state = viewModel.uiState.value
        assertTrue(state.registerSuccess)
        assertTrue(state.isLogged)
        assertEquals(user, state.loggedUser)
    }

    @Test
    fun `register failure updates error state`() {
        val errorMessage = "Registration failed"
        coEvery { repository.register(any(), any(), any(), any(), any(), any()) } returns Result.failure(Exception(errorMessage))
        
        viewModel.register { }

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertFalse(state.registerSuccess)
        assertEquals(errorMessage, state.error)
    }

    @Test
    fun `clearError removes error from state`() {
        coEvery { repository.login(any(), any()) } returns Result.failure(Exception("Error"))
        viewModel.login { }
        
        assertNotNull(viewModel.uiState.value.error)
        
        viewModel.clearError()
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `clearRegisterSuccess updates state`() {
        coEvery { repository.register(any(), any(), any(), any(), any(), any()) } returns Result.success(User())
        viewModel.register { }
        assertTrue(viewModel.uiState.value.registerSuccess)
        
        viewModel.clearRegisterSuccess()
        assertFalse(viewModel.uiState.value.registerSuccess)
    }
}
