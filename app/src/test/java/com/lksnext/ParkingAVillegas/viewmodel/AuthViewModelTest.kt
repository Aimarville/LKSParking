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
    fun `initial state has empty email`() {
        assertEquals("", viewModel.uiState.value.email)
    }

    @Test
    fun `initial state has no error`() {
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `updateName updates state`() {
        viewModel.updateName("John")
        assertEquals("John", viewModel.uiState.value.name)
    }

    @Test
    fun `updateEmail updates state`() {
        viewModel.updateEmail("test@lks.com")
        assertEquals("test@lks.com", viewModel.uiState.value.email)
    }

    @Test
    fun `updatePhone updates state`() {
        viewModel.updatePhone("123")
        assertEquals("123", viewModel.uiState.value.phone)
    }

    @Test
    fun `updateDepartment updates state`() {
        viewModel.updateDepartment("IT")
        assertEquals("IT", viewModel.uiState.value.department)
    }

    @Test
    fun `updatePassword updates state`() {
        viewModel.updatePassword("pass")
        assertEquals("pass", viewModel.uiState.value.password)
    }

    @Test
    fun `updateConfirmPassword updates state`() {
        viewModel.updateConfirmPassword("pass")
        assertEquals("pass", viewModel.uiState.value.confirmPassword)
    }

    @Test
    fun `login success sets isLogged true`() {
        val user = User(uid = "123")
        coEvery { repository.login(any(), any()) } returns Result.success(user)
        viewModel.login { }
        assertTrue(viewModel.uiState.value.isLogged)
    }

    @Test
    fun `login success sets loggedUser`() {
        val user = User(uid = "123")
        coEvery { repository.login(any(), any()) } returns Result.success(user)
        viewModel.login { }
        assertEquals(user, viewModel.uiState.value.loggedUser)
    }

    @Test
    fun `login failure sets error message`() {
        coEvery { repository.login(any(), any()) } returns Result.failure(Exception("Failed"))
        viewModel.login { }
        assertEquals("Failed", viewModel.uiState.value.error)
    }

    @Test
    fun `register success sets registerSuccess flag`() {
        coEvery { repository.register(any(), any(), any(), any(), any(), any()) } returns Result.success(User())
        viewModel.register { }
        assertTrue(viewModel.uiState.value.registerSuccess)
    }

    @Test
    fun `clearError removes error`() {
        coEvery { repository.login(any(), any()) } returns Result.failure(Exception("Error"))
        viewModel.login { }
        viewModel.clearError()
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `clearRegisterSuccess resets flag`() {
        coEvery { repository.register(any(), any(), any(), any(), any(), any()) } returns Result.success(User())
        viewModel.register { }
        viewModel.clearRegisterSuccess()
        assertFalse(viewModel.uiState.value.registerSuccess)
    }
}
