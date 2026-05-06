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
import com.example.lksparking.ui.theme.OrangeLKS
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage // Necesitarás la librería Coil para cargar imágenes
import com.example.lksparking.data.UserRepository
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue

@Composable
fun ProfileScreen(
    userEmail: String, // Pasamos el email en lugar del objeto User entero
    userRepository: UserRepository,
    onNavigateToVehicles: () -> Unit
) {
    // 1. OBTENER ESTADO ACTUALIZADO
    // Buscamos al usuario en la lista observable del repo
    val user = userRepository.usersState.find { it.email == userEmail }
    val context = LocalContext.current

    // 2. ESTADOS PARA EDICIÓN
    var isEditing by remember { mutableStateOf(false) }
    var editNombre by remember { mutableStateOf(user?.nombre ?: "") }
    var editTelefono by remember { mutableStateOf(user?.telefono ?: "") }
    var editDept by remember { mutableStateOf(user?.departamento ?: "") }
    var photoUri by remember { mutableStateOf<Uri?>(user?.profilePhotoUri?.let { Uri.parse(it) }) }

    // 3. LAUNCHER PARA LA CÁMARA/GALERÍA
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        photoUri = uri
        // Opcional: Guardar inmediatamente en el repo
        userRepository.updateProfile(userEmail, editNombre, editTelefono, editDept, uri?.toString())
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5)).padding(16.dp)) {
        Text("Mi Perfil", fontSize = 32.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // --- AVATAR FUNCIONAL ---
                    Box(
                        contentAlignment = Alignment.BottomEnd,
                        modifier = Modifier.clickable { launcher.launch("image/*") }
                    ) {
                        Surface(
                            modifier = Modifier.size(80.dp),
                            shape = CircleShape,
                            color = OrangeLKS
                        ) {
                            if (photoUri != null) {
                                AsyncImage(
                                    model = photoUri,
                                    contentDescription = "Foto de perfil",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop,
                                    // Opcional: añadir un placeholder por si tarda en cargar
                                    error = null,
                                    fallback = null
                                )
                            } else {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(user?.nombre?.firstOrNull()?.toString() ?: "?", color = Color.White, fontSize = 40.sp)
                                }
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
                        Text(user?.nombre ?: "", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Text(user?.email ?: "", fontSize = 14.sp, color = Color.Gray)
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), thickness = 0.5.dp)

                // --- CAMPOS EDITABLES ---
                ProfileField(label = "Nombre Completo *", value = editNombre, isReadOnly = !isEditing) { editNombre = it }
                ProfileField(label = "Teléfono *", value = editTelefono, isReadOnly = !isEditing) { editTelefono = it }
                ProfileField(label = "Departamento *", value = editDept, isReadOnly = !isEditing) { editDept = it }

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (isEditing) {
                            // GUARDAR CAMBIOS
                            userRepository.updateProfile(userEmail, editNombre, editTelefono, editDept, photoUri?.toString())
                            Toast.makeText(context, "Perfil actualizado", Toast.LENGTH_SHORT).show()
                        }
                        isEditing = !isEditing
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isEditing) Color.Black else OrangeLKS),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isEditing) "GUARDAR CAMBIOS" else "EDITAR PERFIL")
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // --- CARD MIS VEHICULOS (ACTUALIZADA) ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.DirectionsCar, null, tint = OrangeLKS, modifier = Modifier.size(32.dp))
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Mis Vehículos", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    // ESTO SE ACTUALIZA SOLO porque el 'user' viene de la lista observable del repo
                    val vehicleCount = user?.vehiculos?.size ?: 0
                    Text("$vehicleCount vehículos registrados", fontSize = 12.sp, color = Color.Gray)
                }
                OutlinedButton(onClick = onNavigateToVehicles) {
                    Text("VER")
                }
            }
        }
    }
}

@Composable
fun ProfileField(label: String, value: String, isReadOnly: Boolean, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            readOnly = isReadOnly,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (isReadOnly) Color.LightGray else OrangeLKS,
                unfocusedBorderColor = Color.LightGray
            )
        )
    }
}