package com.lksnext.ParkingAVillegas.ui.components.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lksnext.ParkingAVillegas.model.User
import com.lksnext.ParkingAVillegas.ui.theme.OrangeLKS

@Composable
fun DrawerHeader(
    user: User?
) {

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

            Box(
                contentAlignment = Alignment.Center
            ) {

                val initial =
                    user?.nombre
                        ?.firstOrNull()
                        ?.toString()
                        ?: "?"

                Text(
                    text = initial,
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text = user?.nombre ?: "Usuario",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = user?.email ?: "correo@empresa.com",
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 14.sp
        )
    }
}