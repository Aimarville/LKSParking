package com.example.lksparking.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lksparking.ui.theme.BlueInfoBg
import com.example.lksparking.ui.theme.LKSParkingTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyReservationsScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }
    var tabs = listOf("VIGENTES (0)", "HISTORICAS (0)")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Titulo de la seccion
        Text(
            text = "Mis Reservas",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        // Pestañas (Tabs)
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            contentColor = MaterialTheme.colorScheme.primary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = {selectedTab = index},
                    text = {
                        Text(
                            title,
                            color = if (selectedTab == index) MaterialTheme.colorScheme.primary else Color.Gray,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                )
            }
        }

        // Contenido segun la pestaña (Aviso azul)
        val infoText = if (selectedTab == 0) {
            "No tienes reservas vigentes en este momento."
        } else {
            "No hay reservas históricas del último mes."
        }

        Box(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BlueInfoBg, RoundedCornerShape(8.dp))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                Spacer(Modifier.width(12.dp))
                Text(
                    text = infoText,
                    color = Color.Black,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyReservationsScreenPreview() {
    LKSParkingTheme {
        MyReservationsScreen()
    }
}