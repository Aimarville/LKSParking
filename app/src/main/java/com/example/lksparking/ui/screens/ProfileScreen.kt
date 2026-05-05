package com.example.lksparking.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.text.style.TextAlign
import com.example.lksparking.model.User
import com.example.lksparking.ui.theme.OrangeLKS

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    user: User?,
    onNavigateToVehicles: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        Text("Mi Perfil", fontSize = 32.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Avatar con inicial y cámara
                    Box(contentAlignment = Alignment.BottomEnd) {
                        Surface(
                            modifier = Modifier.size(80.dp),
                            shape = CircleShape,
                            color = OrangeLKS
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                val initial = user?.nombre?.firstOrNull()?.toString() ?: "?"
                                Text(initial, color = Color.White, fontSize = 40.sp)
                            }
                        }
                        Surface(
                            modifier = Modifier.size(24.dp),
                            shape = CircleShape,
                            color = OrangeLKS,
                            border = BorderStroke(2.dp, Color.White)
                        ) {
                            Icon(Icons.Default.PhotoCamera, null, tint = Color.White, modifier = Modifier.padding(4.dp))
                        }
                    }
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(user?.nombre ?: "Nombre", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Text(user?.email ?: "correo@empresa.com", fontSize = 14.sp, color = Color.Gray)
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), thickness = 0.5.dp)

                // Campos de información
                ProfileField(label = "Nombre Completo *", value = user?.nombre ?: "")
                ProfileField(label = "Correo Corporativo *", value = user?.email ?: "")
                ProfileField(label = "Teléfono *", value = user?.telefono ?: "")
                ProfileField(label = "Departamento *", value = user?.departamento ?: "")

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = { /* Proximamente: Editar */ },
                    colors = ButtonDefaults.buttonColors(containerColor = OrangeLKS),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("EDITAR PERFIL")
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // --- CARD MIS VEHICULOS ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.DirectionsCar, null, tint = OrangeLKS, modifier = Modifier.size(32.dp))
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Mis Vehículos", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        val vehicleCount = user?.vehiculos?.size ?: 0
                        Text("$vehicleCount vehículos registrados", fontSize = 12.sp, color = Color.Gray)
                    }
                }
                OutlinedButton(
                    onClick = { onNavigateToVehicles() },
                    border = BorderStroke(1.dp, OrangeLKS),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text("VER\nVEHÍCULOS", fontSize = 14.sp, color = OrangeLKS, textAlign = TextAlign.Center)
                }
            }
        }
    }
}

@Composable
fun ProfileField(label: String, value: String, helper: String? = null) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            label = {Text(label)},
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.LightGray,
                unfocusedBorderColor = Color.LightGray
            )
        )
        if (helper != null) {
            Text(helper, fontSize = 11.sp, color = Color.Gray, modifier = Modifier.padding(start = 8.dp, top = 2.dp))
        }
    }
}
