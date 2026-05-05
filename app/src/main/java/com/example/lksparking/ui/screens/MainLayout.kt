package com.example.lksparking.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lksparking.model.User
import com.example.lksparking.ui.theme.OrangeLKS
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainLayout(
    currentUser: User?,
    currentRoute: String,
    onNavigate: (String) -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    // Estado para abrir/cerrar el menú lateral
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    // Estado para el panel de notificaciones
    var showNotifications by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    drawerContainerColor = Color.White,
                    modifier = Modifier.width(300.dp)
                ) {
                    // --- CABECERA NARANJA DEL MENU ---
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(OrangeLKS)
                            .padding(24.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(60.dp),
                            shape = CircleShape,
                            color = Color(0xFFBF360C)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                val initial = currentUser?.nombre?.firstOrNull()?.toString() ?: "?"
                                Text(initial, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = currentUser?.nombre ?: "Usuario",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = currentUser?.email ?: "correo@empresa.com",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    // --- OPCIONES DEL MENU ---
                    DrawerItem("Mis Reservas", Icons.Default.DateRange, currentRoute == "my_reservations") {
                        onNavigate("my_reservations")
                        scope.launch { drawerState.close() }
                    }
                    DrawerItem("Nueva Reserva", Icons.Default.AssignmentTurnedIn, currentRoute == "new_reservation") {
                        onNavigate("new_reservation")
                        scope.launch { drawerState.close() }
                    }
                    DrawerItem("Perfil", Icons.Default.Person, currentRoute == "profile") {
                        onNavigate("profile")
                        scope.launch { drawerState.close() }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp)

                    // --- OPCIÓN CERRAR SESIÓN ---
                    DrawerItem("Cerrar Sesión", Icons.AutoMirrored.Filled.Logout, false) {
                        onNavigate("login")
                        scope.launch { drawerState.close() }
                    }
                }
            }
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.DirectionsCar, null, tint = Color.White)
                                Spacer(Modifier.width(8.dp))
                                Text("Garaje", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = {
                                scope.launch { drawerState.open() }
                            }) {
                                Icon(Icons.Default.Menu, null, tint = Color.White)
                            }
                        },
                        actions = {
                            IconButton(onClick = { showNotifications = true }) {
                                BadgedBox(badge = { Badge { Text("2") } }) {
                                    Icon(Icons.Default.Notifications, null, tint = Color.White)
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = OrangeLKS)
                    )
                },
                bottomBar = {
                    NavigationBar(containerColor = Color.White) {
                        NavigationBarItem(
                            selected = currentRoute == "my_reservations",
                            onClick = { onNavigate("my_reservations") },
                            label = { Text("Mis Reservas") },
                            icon = { Icon(Icons.Default.DateRange, null) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = OrangeLKS,
                                selectedTextColor = OrangeLKS,
                                indicatorColor = Color.Transparent
                            )
                        )
                        NavigationBarItem(
                            selected = currentRoute == "new_reservation",
                            onClick = { onNavigate("new_reservation") },
                            label = { Text("Nueva Reserva") },
                            icon = { Icon(Icons.Default.AssignmentTurnedIn, null) }
                        )
                        NavigationBarItem(
                            selected = currentRoute == "profile",
                            onClick = { onNavigate("profile") },
                            label = { Text("Perfil") },
                            icon = { Icon(Icons.Default.Person, null) }
                        )
                    }
                }
            ) { innerPadding ->
                content(innerPadding)
            }
        }

        // Overlay oscuro para el fondo cuando se abren las notificaciones
        if (showNotifications) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .clickable { showNotifications = false }
            )
        }

        // Panel de notificaciones que aparece desde la derecha
        Box(modifier = Modifier.align(Alignment.CenterEnd)) {
            NotificationsPanel(
                isVisible = showNotifications,
                onClose = { showNotifications = false },
                onMarkAllAsRead = { /* Lógica para marcar todas como leídas */ }
            )
        }
    }
}

// Componente auxiliar para los items del menú lateral
@Composable
fun DrawerItem(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, isSelected: Boolean, onClick: () -> Unit) {
    NavigationDrawerItem(
        label = { Text(label, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
        selected = isSelected,
        onClick = onClick,
        icon = { Icon(icon, contentDescription = null) },
        colors = NavigationDrawerItemDefaults.colors(
            unselectedContainerColor = Color.Transparent,
            selectedContainerColor = Color.Transparent,
            selectedIconColor = OrangeLKS,
            selectedTextColor = OrangeLKS,
            unselectedIconColor = Color.Gray,
            unselectedTextColor = Color.Black
        ),
        modifier = Modifier.padding(horizontal = 12.dp)
    )
}
