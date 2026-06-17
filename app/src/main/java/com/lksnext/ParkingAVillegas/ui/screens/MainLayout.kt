package com.lksnext.ParkingAVillegas.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.lksnext.ParkingAVillegas.ui.components.main.MainBottomBar
import com.lksnext.ParkingAVillegas.ui.components.main.MainDrawer
import com.lksnext.ParkingAVillegas.ui.components.main.MainTopBar
import com.lksnext.ParkingAVillegas.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainLayout(
    viewModel: MainViewModel,
    onNavigate: (String) -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {

    val uiState by viewModel.uiState.collectAsState()

    val drawerState =
        rememberDrawerState(
            initialValue = DrawerValue.Closed
        )

    val scope = rememberCoroutineScope()

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {

        Box {

            ModalNavigationDrawer(
                drawerState = drawerState,

                drawerContent = {

                    MainDrawer(
                        uiState = uiState,

                        onNavigate = {

                            viewModel.navigate(it)

                            onNavigate(it)

                            scope.launch {
                                drawerState.close()
                            }
                        }
                    )
                }
            ) {

                Scaffold(

                    topBar = {

                        MainTopBar(

                            notificationCount =
                                uiState.notificationCount,

                            onMenuClick = {

                                scope.launch {
                                    drawerState.open()
                                }
                            },

                            onNotificationsClick = {
                                viewModel.showNotifications()
                            }
                        )
                    },

                    bottomBar = {

                        MainBottomBar(

                            currentRoute =
                                uiState.currentRoute,

                            onNavigate = {

                                viewModel.navigate(it)

                                onNavigate(it)
                            }
                        )
                    }

                ) { innerPadding ->

                    content(innerPadding)
                }
            }

            if (uiState.showNotifications) {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Color.Black.copy(alpha = 0.4f)
                        )
                        .clickable {
                            viewModel.hideNotifications()
                        }
                )
            }

            Box(
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {

                NotificationsPanel(
                    isVisible = uiState.showNotifications,

                    onClose = {
                        viewModel.hideNotifications()
                    },

                    onMarkAllAsRead = {
                        viewModel.markAllNotificationsAsRead()
                    }
                )
            }
        }
    }
}