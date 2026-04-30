package com.example.lksparking.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lksparking.ui.theme.LKSParkingTheme
import com.example.lksparking.ui.theme.OrangeLKS

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainLayout(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    content: @Composable (PaddingValues) -> Unit
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
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Menu, null, tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        BadgedBox(badge = { Badge {Text("2")} }) {
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
                    onClick = {onNavigate("my_reservations")},
                    label = {Text("Mis Reservas")},
                    icon = {Icon(Icons.Default.DateRange, null)},
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = OrangeLKS,
                        selectedTextColor = OrangeLKS,
                        indicatorColor = Color.Transparent
                    )
                )
                NavigationBarItem(
                    selected = currentRoute == "new_reservation",
                    onClick = {onNavigate("new_reservation")},
                    label = {Text("Nueva Reserva")},
                    icon = {Icon(Icons.Default.AssignmentTurnedIn, null)}
                )
                NavigationBarItem(
                    selected = currentRoute == "profile",
                    onClick = {onNavigate("profile")},
                    label = {Text("Perfil")},
                    icon = {Icon(Icons.Default.Person, null)}
                )
            }
        }
    ) {innerPadding ->
        content(innerPadding)
    }
}