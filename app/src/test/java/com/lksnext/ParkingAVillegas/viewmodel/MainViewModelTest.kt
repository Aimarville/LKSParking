package com.lksnext.ParkingAVillegas.viewmodel

import com.lksnext.ParkingAVillegas.model.User
import org.junit.Assert.*
import org.junit.Test

class MainViewModelTest {

    @Test
    fun `initial state with user sets currentUser`() {
        val user = User(uid = "1", nombre = "Test")
        val viewModel = MainViewModel(user)
        assertEquals(user, viewModel.uiState.value.currentUser)
    }

    @Test
    fun `navigate updates currentRoute`() {
        val viewModel = MainViewModel(null)
        viewModel.navigate("test_route")
        assertEquals("test_route", viewModel.uiState.value.currentRoute)
    }

    @Test
    fun `showNotifications sets visibility true`() {
        val viewModel = MainViewModel(null)
        viewModel.showNotifications()
        assertTrue(viewModel.uiState.value.showNotifications)
    }

    @Test
    fun `hideNotifications sets visibility false`() {
        val viewModel = MainViewModel(null)
        viewModel.showNotifications()
        viewModel.hideNotifications()
        assertFalse(viewModel.uiState.value.showNotifications)
    }

    @Test
    fun `markAllNotificationsAsRead resets count`() {
        val viewModel = MainViewModel(null)
        viewModel.markAllNotificationsAsRead()
        assertEquals(0, viewModel.uiState.value.notificationCount)
    }
}
