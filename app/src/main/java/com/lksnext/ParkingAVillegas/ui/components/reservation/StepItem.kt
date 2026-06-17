package com.lksnext.ParkingAVillegas.ui.components.reservation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lksnext.ParkingAVillegas.ui.theme.OrangeLKS

@Composable
fun StepItem(
    number: String,
    text: String,
    isActive: Boolean,
    isCompleted: Boolean
) {

    val color =
        if (isActive || isCompleted)
            OrangeLKS
        else
            Color.Gray.copy(alpha = 0.5f)

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {

        Surface(
            shape = CircleShape,
            color = color,
            modifier = Modifier.size(28.dp)
        ) {

            Box(
                contentAlignment = Alignment.Center
            ) {

                if (isCompleted) {

                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )

                } else {

                    Text(
                        text = number,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = text,
            fontSize = 11.sp,
            lineHeight = 14.sp,
            color =
                if (isActive || isCompleted)
                    Color.Black
                else
                    Color.Gray,
            fontWeight =
                if (isActive || isCompleted)
                    FontWeight.Bold
                else
                    FontWeight.Normal
        )
    }
}