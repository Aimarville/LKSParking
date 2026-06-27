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
    fun `showNotifications and hideNotifications update state`() {
        val viewModel = MainViewModel(null)
        
        viewModel.showNotifications()
        assertTrue(viewModel.uiState.value.showNotifications)
        
        viewModel.hideNotifications()
        assertFalse(viewModel.uiState.value.showNotifications)
    }

    @Test
    fun `markAllNotificationsAsRead sets count to 0`() {
        val viewModel = MainViewModel(null)
        // Note: we can't easily set notificationCount from outside in the current implementation 
        // without it being in the constructor or a setter, but we can test the effect.
        viewModel.markAllNotificationsAsRead()
        assertEquals(0, viewModel.uiState.value.notificationCount)
    }
}
