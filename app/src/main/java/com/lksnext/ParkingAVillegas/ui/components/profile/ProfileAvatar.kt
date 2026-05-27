package com.lksnext.ParkingAVillegas.ui.components.profile

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.lksnext.ParkingAVillegas.ui.theme.OrangeLKS

@Composable
fun ProfileAvatar(
    userName: String,
    photoUri: Uri?,
    onClick: () -> Unit
) {

    Box(
        contentAlignment = Alignment.BottomEnd,
        modifier = Modifier.clickable { onClick() }
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
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

            } else {

                Box(contentAlignment = Alignment.Center) {

                    Text(
                        text = userName.firstOrNull()?.toString() ?: "?",
                        color = Color.White,
                        fontSize = 40.sp
                    )
                }
            }
        }

        Surface(
            modifier = Modifier.size(24.dp),
            shape = CircleShape,
            color = OrangeLKS,
            border = BorderStroke(2.dp, Color.White)
        ) {

            Icon(
                imageVector = Icons.Default.PhotoCamera,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}