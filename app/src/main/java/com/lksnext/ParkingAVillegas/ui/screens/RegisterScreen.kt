package com.lksnext.ParkingAVillegas.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lksnext.ParkingAVillegas.ui.theme.GrayText
import com.lksnext.ParkingAVillegas.ui.theme.OrangeLKS
import com.lksnext.ParkingAVillegas.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    modifier: Modifier = Modifier,
    onLoginClick: () -> Unit = {},
    onRegisterSuccess: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    var passwordVisible by remember {
        mutableStateOf(false)
    }

    var context = LocalContext.current

    // Escuchar errores
    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            Toast.makeText(
                context,
                it,
                Toast.LENGTH_SHORT
            ).show()

            viewModel.clearError()
        }
    }

    // Exito registro
    LaunchedEffect(uiState.registerSuccess) {
        if (uiState.registerSuccess) {
            Toast.makeText(
                context,
                "Registro exitoso",
                Toast.LENGTH_SHORT
            ).show()

            viewModel.clearRegisterSuccess()

            onRegisterSuccess()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.DirectionsCar,
                    contentDescription = "Car Icon",
                    modifier = Modifier.size(64.dp),
                    tint = OrangeLKS
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Crear Cuenta",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Text(
                    text = "Regístrate con tu correo corporativo",
                    fontSize = 14.sp,
                    color = GrayText,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                // NAME
                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = {
                        viewModel.updateName(it)
                    },
                    label = { Text("Nombre Completo *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // EMAIL
                Column {
                    OutlinedTextField(
                        value = uiState.email,
                        onValueChange = {
                            viewModel.updateEmail(it)
                        },
                        label = { Text("Correo Corporativo *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )
                    Text(
                        text = "Debe ser un correo @lks.com",
                        fontSize = 12.sp,
                        color = GrayText,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // PHONE
                OutlinedTextField(
                    value = uiState.phone,
                    onValueChange = {
                        viewModel.updatePhone(it)
                    },
                    label = { Text("Teléfono *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // DEPARTMENT
                OutlinedTextField(
                    value = uiState.department,
                    onValueChange = {
                        viewModel.updateDepartment(it)
                    },
                    label = { Text("Departamento *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // PASSWORD
                OutlinedTextField(
                    value = uiState.password,
                    onValueChange = {
                        viewModel.updatePassword(it)
                    },
                    label = { Text("Contraseña *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, contentDescription = null, tint = GrayText)
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // CONFIRM PASSWORD
                OutlinedTextField(
                    value = uiState.confirmPassword,
                    onValueChange = {
                        viewModel.updateConfirmPassword(it)
                    },
                    label = { Text("Confirmar Contraseña *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, contentDescription = null, tint = GrayText)
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        viewModel.register {
                            onRegisterSuccess()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OrangeLKS),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "REGISTRARSE",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row {
                    Text(
                        text = "¿Ya tienes cuenta? ",
                        fontSize = 14.sp,
                        color = GrayText
                    )
                    Text(
                        text = "Inicia Sesión",
                        fontSize = 14.sp,
                        color = OrangeLKS,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onLoginClick() }
                    )
                }
            }
        }
    }
}
